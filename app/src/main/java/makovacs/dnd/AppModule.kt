package makovacs.dnd

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.AuthRepositoryFirebase
import makovacs.dnd.data.dnd.users.ProfileRepository
import makovacs.dnd.data.dnd.users.ProfileRepositoryDataStore

class AppModule(
    private val appContext: Context
) {
    val profileRepository : ProfileRepository by lazy {
        ProfileRepositoryDataStore(appContext)
    }
    val authRepository : AuthRepository by lazy {
        AuthRepositoryFirebase(Firebase.auth)
    }
}