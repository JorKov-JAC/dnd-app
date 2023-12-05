package makovacs.dnd.data.dnd.db.magicitems

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import makovacs.dnd.data.dnd.MagicItem
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.ProfileData

class MagicItemsRepository(val db: FirebaseFirestore, val auth: AuthRepository) {
    val dbMagicId: CollectionReference = db.collection("MagicItems")
    suspend fun saveMagicItem(item: MagicItem) {

        if(auth.currentUser().value != null)
        {
            //dbMagicId.document(auth.currentUser().value.toString()).set(item)

            val itemDb = dbMagicId.document(auth.currentUser().value?.email ?: "null")
                .collection("Items")

            itemDb.add(item)
                .addOnSuccessListener {
                    println("Item saved.")
                }
                .addOnFailureListener { e ->
                    println("Error saving item: $e")
                }
        }
        else
        {
            println("user not logged in")
        }
    }

    suspend fun getMagicItems(): Flow<List<MagicItem>> = callbackFlow {
        val document = dbMagicId.document(auth.currentUser().value?.email ?: "null")
            .collection("Items")

        val subscription = document.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                println("Listen failed: $error")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val item = snapshot.toObjects(MagicItem::class.java)
                if (item != null) {
                    println("Real-time update to item")
                    trySend(item)
                } else {
                    println("Item is / has become null")
                    trySend(listOf<MagicItem>()) // If there is no saved profile, then send a default object
                }
            } else {
                // The user document does not exist or has no data
                println("Item does not exist")
                trySend(listOf<MagicItem>()) // send default object
            }
        }
        awaitClose { subscription.remove() }

    }

    suspend fun delete(name: String)
    {
        if (auth.currentUser().value != null) {
            val collection = dbMagicId.document(auth.currentUser().value?.email ?: "null")
                .collection("Items")

            collection.whereEqualTo("name", name).get().addOnSuccessListener { documents ->
                for(document in documents)
                {
                    collection.document(document.id)
                        .delete()
                        .addOnSuccessListener { println("Item $name successfully deleted!") }
                        .addOnFailureListener { error -> println("Error deleting item $name: $error") }
                }
            }


        } else {
            println("Delete failed: User is not authenticated")
        }
    }



}