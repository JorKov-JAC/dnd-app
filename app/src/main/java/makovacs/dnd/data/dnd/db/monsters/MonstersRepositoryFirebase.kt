package makovacs.dnd.data.dnd.db.monsters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.storage
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
import java.io.ByteArrayOutputStream
import kotlin.math.min

class MonstersRepositoryFirebase(val authRepository: AuthRepository) : MonstersRepository {
	/**
	 * A class which contains the serializable data for a [Monster].
	 *
	 * Almost all fields in this class match a field with the same name in [Monster].
	 *
	 * @param firestoreDocRef A reference to this monster's firestore document.
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
			null,
			emptyList(),
			emptyList()
		)

		companion object {
			fun fromMonster(monster: Monster) = monster.run { FirestoreMonster(
				null,
				name,
				rawDescription,
				size,
				armorClass,
				hitDiceCount,
				speed,
				abilityScores.scoreList,
				challengeRating,
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

	companion object {
		const val MAX_IMAGE_LENGTH = 512
		const val MAX_IMAGE_DOWNLOAD_BYTES = 20L * 1024 * 1024 // 20 MiB
	}

	val collection = Firebase.firestore.collection("monsters")
	val imagesStorage = Firebase.storage.reference.child("monsters/images")

	override suspend fun addMonster(monster: Monster) {
//		val user = authRepository.currentUser().value

		val firestoreMonster = FirestoreMonster.fromMonster(monster)
		val docRef = collection.add(firestoreMonster).await()
		firestoreMonster.id = docRef.id

		// Store image bitmap
		val bitmap = monster.imageBitmap
		if (bitmap != null) {
			val bitmapStream = ByteArrayOutputStream()

			val aspectRatio = bitmap.width.toFloat() / bitmap.height
			val width = min(
				MAX_IMAGE_LENGTH,
				(MAX_IMAGE_LENGTH * aspectRatio).toInt().coerceAtLeast(1)
			)
			val height = (width / aspectRatio).toInt().coerceAtLeast(1)

			bitmap
				// Limit size
				.scale(width, height)
				// Compress and re-encode
				.compress(
					Bitmap.CompressFormat.PNG,
					/* Ignored by PNG */ 100,
					bitmapStream
				)

			imagesStorage
				.child("${firestoreMonster.id!!}.png")
				.putBytes(bitmapStream.toByteArray())
				.await()
		}
	}

	private suspend fun getImage(id: String): Bitmap? {
		return try {
			val imageBitmapBytes = imagesStorage
				.child("$id.png")
				.getBytes(MAX_IMAGE_DOWNLOAD_BYTES)
				.await()

			BitmapFactory.decodeByteArray(
				imageBitmapBytes,
				0,
				imageBitmapBytes.size
			)
		} catch (_: Throwable) {
			null
		}
	}

	private fun createMonster(firestoreMonster: FirestoreMonster, imageBitmap: Bitmap?): Monster {
		return firestoreMonster.run { Monster(
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
					InformationEntryTypes.SEPARATOR -> Separator()
					InformationEntryTypes.DESCRIPTION -> Description(title = it["title"] as String, text = it["text"] as String)
					InformationEntryTypes.HEADER -> Header(text = it["text"] as String)
				}
			})
		) }
	}

	override suspend fun getMonster(name: String): Monster? {
		val queryResults = collection
			.whereEqualTo("name", name)
			.limit(1) // Optimization; might hit local cache first
			.get()
			.await()
			.toObjects<FirestoreMonster>()

		if (queryResults.isEmpty()) return null

		val firestoreMonster = queryResults[0]

		println("=========")
		println("getMonster")
		println("---------")
		println(firestoreMonster)
		println(firestoreMonster.id)
		println("=========")

		return createMonster(firestoreMonster, getImage(firestoreMonster.id!!))
	}

	override suspend fun queryMonsters(query: MonsterQuery): List<Monster> {
		val monsters = collection.get().await().toObjects<FirestoreMonster>()
		monsters.forEach {
			println("=========")
			println("queryMonsters")
			println("---------")
			println(it)
			println(it.id)
			println("=========")
		}
		return monsters.map {
			createMonster(
				it,
				getImage(it.id!!)
			)
		}
	}
}