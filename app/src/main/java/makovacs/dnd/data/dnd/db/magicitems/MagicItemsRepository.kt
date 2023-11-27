package makovacs.dnd.data.dnd.db.magicitems

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import makovacs.dnd.data.dnd.MagicItem

class MagicItemsRepository(val db: FirebaseFirestore) {
    val dbMagicId: CollectionReference = db.collection("MagicItems")

    suspend fun saveMagicItem(item: MagicItem, email: String) {

        dbMagicId.document(email).set(item)
            .addOnSuccessListener {
                println("Profile saved.")
            }
            .addOnFailureListener { e ->
                println("Error saving item: $e")
            }
    }

    /*
    suspend fun getMagicItems(email: String): Flow<MagicItem> = callbackFlow {

        val document = dbMagicId.document(email)
        val subscription = document.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                println("Listen failed: $error")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val item = snapshot.toObject(MagicItem::class.java)
                if (item != null) {
                    println("Real-time update to item")
                    trySend(item)
                } else {
                    println("Item is / has become null")
                    trySend(MagicItem()) // If there is no saved profile, then send a default object
                }
            } else {
                // The user document does not exist or has no data
                println("Profile does not exist")
                trySend(ProfileData()) // send default object
            }
        }
        awaitClose { subscription.remove() }
    }*/

}