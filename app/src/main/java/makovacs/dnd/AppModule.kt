package makovacs.dnd

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import makovacs.dnd.data.dnd.db.magicitems.MagicItemsRepository
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.AuthRepositoryFirebase
import makovacs.dnd.data.dnd.users.ProfileRepository
import makovacs.dnd.data.dnd.users.ProfileRepositoryDataStore

class AppModule(
    private val appContext: Context
) {
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(Firebase.auth)
    }

    val magicItemsRepository : MagicItemsRepository by lazy {
        MagicItemsRepository(FirebaseFirestore.getInstance())
    }

}
