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
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.logic.fit
import makovacs.dnd.logic.generateUid
import java.io.ByteArrayOutputStream
import java.util.WeakHashMap

class MonstersRepositoryFirebase(val authRepository: AuthRepository) : MonstersRepository {
	companion object {
		const val MAX_PNG_SIDE_LENGTH = 256
		const val MAX_JPEG_SIDE_LENGTH = 384
		const val JPEG_QUALITY = 85
		const val MAX_IMAGE_DOWNLOAD_BYTES = 20L * 1024 * 1024 // 20 MiB
	}

	private val collection = Firebase.firestore.collection("monsters")
	private val imagesStorage = Firebase.storage.reference.child("monsters/images")

	private val firestoreMonstersFromMonsters = MutableStateFlow(WeakHashMap<Monster, FirestoreMonster>())
	private val monsterBitmapsFromIds = MutableStateFlow(mapOf<String, Bitmap?>())
	@OptIn(DelicateCoroutinesApi::class)
	private val monsters by lazy {
		val flow = MutableStateFlow<List<FirestoreMonster>?>(null)

		collection.orderBy("name").addSnapshotListener { value, error ->
			flow.update { flow ->
				println("========")
				println("Update started")
				println("========")
				if (error != null) {
					println("Couldn't add snapshot listener for monsters: $error")
					return@addSnapshotListener
				}

				val oldImageMutationCounts = flow?.associate {
					it.id to it.imageMutations
				} ?: emptyMap()

				if (value == null) {
					return@update flow // Do nothing
				}

				val monsters = value.toObjects<FirestoreMonster>()

				monsters.forEach { monster ->
					if ((oldImageMutationCounts[monster.id!!] ?: -1) != monster.imageMutations) {
						// I really don't care if this succeeds, just gonna use GlobalScope
						GlobalScope.launch {
							val image = getImage(monster.id!!)
							monsterBitmapsFromIds.update {
								// TODO Copying map to work around conflation like this is gross
								it + mapOf(monster.id!! to image)
							}
						}
					}
				}

				println("========")
				println("Update finished")
				println("========")
				monsters
			}
		}

		flow
	}

	private suspend fun setMonsterBitmap(firestoreMonsterId: String, imageMutations: Int, bitmap: Bitmap) {
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
			.child(firestoreMonsterId)
			.putBytes(bitmapStream.toByteArray())
			.await()
		collection.document(firestoreMonsterId).update("imageMutations", imageMutations + (0..63).random())
	}

	override suspend fun addMonster(monster: Monster) {
		collection.document(monster.id).set(FirestoreMonster.fromMonster(monster, imageMutations = 0))

		// Store image bitmap
		monster.imageBitmap?.let { setMonsterBitmap(monster.id, 0, it) }
	}

	override suspend fun getMonster(id: String): Flow<Monster?> {
//		val queryResults = collection
//			.whereEqualTo("name", name)
//			.limit(1) // Optimization; might hit local cache first
//			.get()
//			.await()
//			.toObjects<FirestoreMonster>()
//
//		if (queryResults.isEmpty()) return null
//
//		val firestoreMonster = queryResults[0]
//
//		println("=========")
//		println("getMonster")
//		println("---------")
//		println(firestoreMonster)
//		println(firestoreMonster.id)
//		println("=========")
//
//		return createMonster(firestoreMonster, getImage(firestoreMonster.id!!))

		return queryMonsters(MonsterQuery("", emptyList(), emptySet()))
			.map { monsters ->
				monsters?.firstOrNull { it.id == id }
			}
	}

	override fun queryMonsters(query: MonsterQuery): Flow<List<Monster>?> {
//		val monsters = collection.get().await().toObjects<FirestoreMonster>()
//		monsters.forEach {
//			println("=========")
//			println("queryMonsters")
//			println("---------")
//			println(it)
//			println(it.id)
//			println("=========")
//		}
//		return monsters.map {
//			createMonster(
//				it,
//				getImage(it.id!!)
//			)
//		}
		return monsters.combine(monsterBitmapsFromIds) { monsters, monsterBitmapsFromIds ->
			monsters?.map {
				createMonster(it, monsterBitmapsFromIds[it.id])
			}
		}
//		return monsters.map { it?.let{it.map {inner ->
//			createMonster(inner, null)
//		} }}
	}

	override suspend fun deleteMonster(monster: Monster) {
		val id = monster.id
		imagesStorage.child(id).delete()
		collection.document(id).delete()
	}

	override suspend fun updateMonster(oldMonster: Monster, newMonster: Monster) {
		val oldFirestoreMonster = firestoreMonstersFromMonsters.value[oldMonster]!!

		val newFirestoreMonster = FirestoreMonster.fromMonster(
			newMonster,
			oldFirestoreMonster.imageMutations
		)

		// Send the update
		collection
			.document(oldMonster.id)
			.set(newFirestoreMonster)
			// Don't await; otherwise it will block when network is down
			//.await()

		if (newMonster.imageBitmap == null) {
			// This deletion is done regardless of if a bitmap existed before in case it just hasn't
			// loaded yet
			imagesStorage.child(oldMonster.id).delete()
		} else if (newMonster.imageBitmap != oldMonster.imageBitmap) {
			// Update the image
			setMonsterBitmap(oldMonster.id, oldFirestoreMonster.imageMutations, newMonster.imageBitmap)
		}


	}

	private suspend fun getImage(id: String): Bitmap? {
		return try {
			val imageBitmapBytes = imagesStorage
				.child(id)
				.getBytes(MAX_IMAGE_DOWNLOAD_BYTES)
				.await()

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

	private fun createMonster(firestoreMonster: FirestoreMonster, imageBitmap: Bitmap?): Monster {
		val monster = firestoreMonster.run { Monster(
			id ?: generateUid(),
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
			Information(information.map {
				when (InformationEntryTypes.values()[(it["type"] as Long).toInt()]) {
					InformationEntryTypes.SEPARATOR -> Separator
					InformationEntryTypes.DESCRIPTION -> Description(title = it["title"] as String?, text = it["text"] as String)
					InformationEntryTypes.HEADER -> Header(text = it["text"] as String)
				}
			})
		) }

		firestoreMonstersFromMonsters.update {
			val copy = WeakHashMap(it)
			copy[monster] = firestoreMonster
			copy
		}

		return monster
	}
}

/**
 * A class which contains the serializable data for a [Monster].
 *
 * Almost all fields in this class match a field with the same name in [Monster].
 *
 * @param id This id of this monster's Firestore document.
 */
private data class FirestoreMonster(
	@DocumentId
	var id: String?,
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
	constructor(): this(
		null,
		"",
		"",
		CreatureSize.TINY,
		0,
		0,
		0,
		emptyList(),
		0f,
		0,
		null,
		emptyList(),
		emptyList()
	)

	companion object {
		fun fromMonster(monster: Monster, imageMutations: Int) = monster.run { FirestoreMonster(
			null,
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
				val map = mutableMapOf<String, Any?>()

				val type = when(it) {
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

				map["type"] = type
				map
			}
		) }
	}
}
