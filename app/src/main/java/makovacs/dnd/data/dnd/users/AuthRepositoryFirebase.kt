package makovacs.dnd.data.dnd.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AuthRepositoryFirebase(private val auth: FirebaseAuth) : AuthRepository {
    private val currentUserStateFlow = MutableStateFlow(auth.currentUser?.toUser())

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            if (it.email == null) {
                null
            } else {
                User(
                    email = it.email!!
                )
            }
        }
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUserStateFlow.value = firebaseAuth.currentUser?.toUser()
        }
    }

    override fun currentUser(): StateFlow<User?> {
        return currentUserStateFlow
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    override fun signOut() {
        return auth.signOut()
    }

    override suspend fun delete() {
        if (auth.currentUser != null) {
            auth.currentUser!!.delete()
        }
    }
}
