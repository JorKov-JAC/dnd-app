package makovacs.dnd.data.dnd.db.monsters

import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.logic.strictUriEncode
import java.io.ByteArrayOutputStream
import kotlin.math.min

class MonstersRepositoryFirebase(val authRepository: AuthRepository) : MonstersRepository {
	companion object {
		private const val cloudStorageImagesPath = "monsters/images/"
	}

	override fun addMonster(monster: Monster) {
//		val user = authRepository.currentUser().value

		val serializableAndNonSerializableData = monster.splitSerializableData()

		// TODO I think I'm actually supposed to only call Firebase.storage and
		//      Firebase.firestore once for performance. Oh well.

		Firebase.firestore
			.collection("monsters")
			.add(serializableAndNonSerializableData.serializableData)

		// Store image bitmap
		val bitmap = serializableAndNonSerializableData.imageBitmap
		if (bitmap != null) {
			val bitmapStream = ByteArrayOutputStream()
			bitmap
				// Limit size
				.scale(min(bitmap.width, 512), min(bitmap.height, 512))
				// Compress and reencode
				.compress(
					Bitmap.CompressFormat.JPEG,
					90,
					bitmapStream
				)

			Firebase.storage
				.reference
				.child("$cloudStorageImagesPath${monster.id.strictUriEncode()}.jpg")
				.putBytes(bitmapStream.toByteArray())
		}
	}
}