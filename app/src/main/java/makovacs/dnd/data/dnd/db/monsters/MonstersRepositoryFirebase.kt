package makovacs.dnd.data.dnd.db.monsters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.storage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.CreatureSize
import makovacs.dnd.data.dnd.Description
import makovacs.dnd.data.dnd.Header
import makovacs.dnd.data.dnd.Information
import makovacs.dnd.data.dnd.InformationEntryTypes
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery
import makovacs.dnd.data.dnd.Separator
import makovacs.dnd.logic.fit
import makovacs.dnd.logic.generateUid
import java.io.ByteArrayOutputStream
import java.util.WeakHashMap

/**
 * An implementation of [MonstersRepository] using Firebase.
 */
class MonstersRepositoryFirebase : MonstersRepository {
    companion object {
        /** Max width and height of a [Monster.imageBitmap] if it's stored as a PNG. */
        const val MAX_PNG_SIDE_LENGTH = 256

        /** Max width and height of a [Monster.imageBitmap] if it's stored as a JPEG. */
        const val MAX_JPEG_SIDE_LENGTH = 384

        /** Quality used to encode [Monster.imageBitmap] as a JPEG. */
        const val JPEG_QUALITY = 85

        /** Maximum size of a monster image to download. */
        const val MAX_IMAGE_DOWNLOAD_BYTES = 10L * 1024 * 1024 // 10 MiB
    }

    /**
     * The Firestore monsters collection.
     */
    private val collection = Firebase.firestore.collection("monsters")

    /**
     * The Firebase Storage directory for monster images.
     */
    private val imagesStorage = Firebase.storage.reference.child("monsters/images")

    /**
     * Map from [Monster]s to their origin [FirestoreMonster]s.
     */
    private val firestoreMonstersFromMonsters = MutableStateFlow(WeakHashMap<Monster, FirestoreMonster>())

    /**
     * Map from [Monster.id]s to their [Monster.imageBitmap]s.
     */
    private val monsterBitmapsFromIds = MutableStateFlow(mapOf<String, Bitmap?>())

    /**
     * All of the monsters in the repository.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val monsters by lazy {
        // The result flow which will provide the monsters:
        val flow = MutableStateFlow<List<FirestoreMonster>?>(null)

        collection.orderBy("name").addSnapshotListener { snapshot, error ->
            flow.update { flow ->
                // Handle errors:
                if (error != null) {
                    println("Couldn't add snapshot listener for monsters: $error")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    return@update flow // Do nothing
                }

                val monsters = snapshot.toObjects<FirestoreMonster>()

                // > Handle monster images
                // Maps every monster's ID to the image mutation number from the last snapshot:
                val oldImageMutationCounts = flow?.associate {
                    it.id to it.imageMutations
                } ?: emptyMap()

                // Update each monster's image if its mutation number has changed:
                for (monster in monsters) {
                    val id = monster.id!!
                    if (oldImageMutationCounts[id]?.equals(monster.imageMutations) != true) {
                        // Update the image in a thread pool:
                        GlobalScope.launch {
                            val image = getImage(id)
                            monsterBitmapsFromIds.update {
                                it + mapOf(id to image)
                            }
                        }
                    }
                }

                monsters
            }
        }

        flow
    }

    // Documented in interface
    override suspend fun addMonster(monster: Monster) {
        // Add monster
        collection
            .document(monster.id)
            .set(FirestoreMonster.fromMonster(monster, imageMutations = 0))

        // Store image bitmap
        monster.imageBitmap?.let { setMonsterImage(monster.id, 0, it) }
    }

    // Documented in interface
    override suspend fun deleteMonster(monster: Monster) {
        val id = monster.id
        imagesStorage.child(id).delete()
        collection.document(id).delete()
    }

    // Documented in interface
    override fun getAtLeast(query: MonsterQuery): Flow<List<Monster>?> {
        return monsters.combine(monsterBitmapsFromIds) { monsters, monsterBitmapsFromIds ->
            monsters?.mapNotNull {
                tryCreateMonster(it, monsterBitmapsFromIds[it.id])
            }
        }
    }

    // Documented in interface
    override suspend fun getMonster(id: String): Flow<Monster?> {
        return getAtLeast(MonsterQuery("", emptyList(), emptySet()))
            .map { monsters ->
                monsters?.firstOrNull { it.id == id }
            }
    }

    // Documented in interface
    override suspend fun updateMonster(oldMonster: Monster, newMonster: Monster) {
        val oldFirestoreMonster = firestoreMonstersFromMonsters.value[oldMonster]!!

        val newFirestoreMonster = FirestoreMonster.fromMonster(
            newMonster,
            oldFirestoreMonster.imageMutations
        )

        // Send the update
        val firestoreTask = collection
            .document(oldMonster.id)
            .set(newFirestoreMonster)

        // Handle updating the image
        if (newMonster.imageBitmap != oldMonster.imageBitmap ||
            // Always delete if new image is null since there might be one that just hasn't
            // loaded yet:
            newMonster.imageBitmap == null
        ) {
            setMonsterImage(
                oldMonster.id,
                oldFirestoreMonster.imageMutations,
                newMonster.imageBitmap
            )
        }

        firestoreTask.await()
    }

    /**
     * Gets the image for the given monster id.
     *
     * @param id The [Monster.id] of the monster to get the image for.
     * @returns The image's bitmap, or null if it could not be retrieved.
     */
    private suspend fun getImage(id: String): Bitmap? {
        return try {
            // Get bitmap bytes:
            val imageBitmapBytes = imagesStorage
                .child(id)
                .getBytes(MAX_IMAGE_DOWNLOAD_BYTES)
                .await()

            // Decode in another thread:
            coroutineScope {
                BitmapFactory.decodeByteArray(
                    imageBitmapBytes,
                    0,
                    imageBitmapBytes.size
                )
            } ?: throw Exception("Could not decode bitmap")
        } catch (ex: Exception) {
            println("Could not get monster image with id \"$id\": $ex")
            null
        }
    }

    /**
     * Updates a monster's image.
     *
     * @param monsterId The ID of the monster to set the image for.
     * @param imageMutations The monster's current image mutation number.
     * @param bitmap The new bitmap to use, or null if the bitmap should be deleted.
     */
    private suspend fun setMonsterImage(monsterId: String, imageMutations: Int, bitmap: Bitmap?) {
        if (bitmap != null) {
            // > Upload new image

            val bitmapStream = ByteArrayOutputStream()

            if (bitmap.hasAlpha()) {
                Bitmap.CompressFormat.PNG
                bitmap
                    // Limit size
                    .fit(MAX_PNG_SIDE_LENGTH, MAX_PNG_SIDE_LENGTH)
                    // Compress and re-encode
                    .compress(
                        Bitmap.CompressFormat.PNG,
                        100, // Ignored by PNG
                        bitmapStream
                    )
            } else {
                bitmap
                    // Limit size
                    .fit(MAX_JPEG_SIDE_LENGTH, MAX_JPEG_SIDE_LENGTH)
                    // Compress and re-encode
                    .compress(
                        Bitmap.CompressFormat.JPEG,
                        JPEG_QUALITY,
                        bitmapStream
                    )
            }

            imagesStorage
                .child(monsterId)
                .putBytes(bitmapStream.toByteArray())
                .await()
        } else {
            // > Delete any existing images

            imagesStorage.child(monsterId).delete()
        }

        // Update firestore monster's mutations so that
        collection
            .document(monsterId)
            .update(
                "imageMutations",
                // Add a random number in case of extremely rare simultaneous updates:
                imageMutations + (0..127).random()
            )
    }

    /**
     * Tries to convert a [FirestoreMonster] to a [Monster].
     *
     * @param firestoreMonster The monster data from Firestore.
     * @param imageBitmap The [Monster.imageBitmap] to use.
     * @return The converted monster, or null if conversion failed.
     */
    private fun tryCreateMonster(firestoreMonster: FirestoreMonster, imageBitmap: Bitmap?): Monster? {
        return try {
            // Create the monster
            val monster = firestoreMonster.run {
                Monster(
                    id ?: generateUid(),
                    ownerUserId,
                    name,
                    description,
                    size,
                    armorClass,
                    hitDiceCount,
                    speed,
                    AbilityScores.from(abilityScores),
                    challengeRating,
                    imageBitmap,
                    imageDesc,
                    tags,
                    Information(
                        information.map {
                            // > Convert map into an entry
                            when (InformationEntryTypes.values()[(it["type"] as Long).toInt()]) {
                                InformationEntryTypes.SEPARATOR -> Separator
                                InformationEntryTypes.DESCRIPTION -> Description(
                                    title = it["title"] as String?,
                                    text = it["text"] as String
                                )
                                InformationEntryTypes.HEADER -> Header(text = it["text"] as String)
                            }
                        }
                    )
                )
            }

            // Associate the firestore data to the created monster
            firestoreMonstersFromMonsters.update {
                val copy = WeakHashMap(it)
                copy[monster] = firestoreMonster
                copy
            }

            monster
        } catch (ex: Exception) {
            println("Couldn't convert Firebase monster $firestoreMonster: $ex")
            null
        }
    }
}

/**
 * A class which contains the serializable data for a [Monster].
 *
 * Almost all fields in this class match a field with the same name in [Monster].
 *
 * @param id This id of this monster's Firestore document.
 * @param imageMutations A number representing the mutation number of the current fetched image
 * bitmap. This number is changed to tell clients to refresh their images.
 */
private data class FirestoreMonster(
    @DocumentId
    var id: String?,
    val ownerUserId: String?,
    val name: String,
    val description: String,
    val size: CreatureSize,
    val armorClass: Int,
    val hitDiceCount: Int,
    val speed: Int,
    val abilityScores: List<Int>,
    val challengeRating: Float,
    var imageMutations: Int,
    val imageDesc: String?,
    val tags: List<String>,
    val information: List<MutableMap<String, Any?>>
) {
    /**
     * Don't use this constructor, it is only used during deserialization.
     */
    private constructor() : this(
        null,
        null,
        "Untitled",
        "",
        CreatureSize.MEDIUM,
        0,
        1,
        0,
        List(6) { AbilityScores.validScoreRange.first },
        0f,
        0,
        null,
        emptyList(),
        emptyList()
    )

    companion object {
        /**
         * Creates a new instance from a given monster.
         *
         * @param monster The monster to base the instance off of.
         * @param imageMutations The [FirestoreMonster.imageMutations] to use.
         */
        fun fromMonster(monster: Monster, imageMutations: Int) = monster.run {
            FirestoreMonster(
                null,
                ownerUserId,
                name,
                rawDescription,
                size,
                armorClass,
                hitDiceCount,
                speed,
                abilityScores.scoreList,
                challengeRating,
                imageMutations,
                imageDesc,
                tags,
                information.entries.map {
                    // > Convert every entry into a map
                    val map = mutableMapOf<String, Any?>()

                    map["type"] = when (it) {
                        is Separator -> InformationEntryTypes.SEPARATOR.ordinal
                        is Description -> {
                            map["title"] = it.title
                            map["text"] = it.text
                            InformationEntryTypes.DESCRIPTION.ordinal
                        }
                        is Header -> {
                            map["text"] = it.text
                            InformationEntryTypes.HEADER.ordinal
                        }
                    }

                    map
                }
            )
        }
    }
}
