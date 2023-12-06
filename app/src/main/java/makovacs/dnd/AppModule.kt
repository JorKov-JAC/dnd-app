package makovacs.dnd

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import makovacs.dnd.data.dnd.db.monsters.MonstersRepository
import makovacs.dnd.data.dnd.db.monsters.MonstersRepositoryFirebase
import makovacs.dnd.data.dnd.db.magicitems.MagicItemsRepository
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.AuthRepositoryFirebase
import makovacs.dnd.data.dnd.users.ProfileRepository
import makovacs.dnd.data.dnd.users.ProfileRepositoryDataStore

class AppModule(
    private val appContext: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryDataStore(appContext)
    }
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(auth)
    }

    val magicItemsRepository : MagicItemsRepository by lazy {
        MagicItemsRepository(FirebaseFirestore.getInstance(), authRepository)
    }


    val monstersRepository: MonstersRepository by lazy {
        MonstersRepositoryFirebase()
    }
}
