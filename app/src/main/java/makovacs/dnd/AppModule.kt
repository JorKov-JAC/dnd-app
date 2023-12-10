// Main coding: Makena & Jordan

package makovacs.dnd

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import makovacs.dnd.data.dnd.db.magicitems.MagicItemsRepository
import makovacs.dnd.data.dnd.db.monsters.MonstersRepository
import makovacs.dnd.data.dnd.db.monsters.MonstersRepositoryFirebase
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.AuthRepositoryFirebase

/**
 * Creates the repositories
 */
class AppModule(
    private val appContext: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(auth)
    }

    val magicItemsRepository: MagicItemsRepository by lazy {
        MagicItemsRepository(FirebaseFirestore.getInstance(), authRepository)
    }

    val monstersRepository: MonstersRepository by lazy {
        MonstersRepositoryFirebase()
    }
}
