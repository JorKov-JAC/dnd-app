package makovacs.dnd.data.dnd.db.magicitems

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import makovacs.dnd.data.dnd.MagicItem
import makovacs.dnd.data.dnd.users.AuthRepository

/*
 * The repository that interacts with the Firestore database containing the magic items
 */
class MagicItemsRepository(val db: FirebaseFirestore, val auth: AuthRepository) {
    val dbMagicId: CollectionReference = db.collection("MagicItems")

    // adds a new magic item to the user's collection of magic items
    suspend fun saveMagicItem(item: MagicItem) {
        if (auth.currentUser().value != null) {
            val itemDb = dbMagicId.document(auth.currentUser().value?.email ?: "null")
                .collection("Items")

            itemDb.add(item)
                .addOnSuccessListener {
                    println("Item saved.")
                }
                .addOnFailureListener { e ->
                    println("Error saving item: $e")
                }
        } else {
            println("user not logged in")
        }
    }

    // get's the user's collection of magic items from the database
    suspend fun getMagicItems(): Flow<List<MagicItem>> = callbackFlow {
        val user = auth.currentUser().value
        if (user == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val document = dbMagicId.document(user.email)
            .collection("Items")

        val subscription = document.addSnapshotListener { snapshot, error ->
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

    // deletes all the magic items in the user's collection with a name matching the passed in one
    suspend fun delete(name: String) {
        if (auth.currentUser().value != null) {
            val collection = dbMagicId.document(auth.currentUser().value?.email ?: "null")
                .collection("Items")

            collection.whereEqualTo("name", name).get().addOnSuccessListener { documents ->
                for (document in documents) {
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

    // to be used when a user is deleting their account to get rid of all the stored items
    suspend fun deleteAll() {
        if (auth.currentUser().value != null) {
            val collection = dbMagicId.document(auth.currentUser().value?.email ?: "null")
                .collection("Items")

            collection.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    collection.document(document.id)
                        .delete()
                        .addOnSuccessListener { println("Items successfully deleted!") }
                        .addOnFailureListener { error -> println("Error deleting items: $error") }
                }
            }
        } else {
            println("Delete failed: User is not authenticated")
        }
    }
}
