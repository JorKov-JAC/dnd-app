package makovacs.dnd.data.dnd.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
/*
 * Main Coding: Makena
 * The authentication firebase repository
 * @property FirebaseAuth The connection to the Firebase database
 */
class AuthRepositoryFirebase(private val auth: FirebaseAuth) : AuthRepository {
    private val currentUserStateFlow = MutableStateFlow(auth.currentUser?.toUser())

    //Returns the current user instance as a User object
    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            if (it.email == null) {
                null
            } else {
                User(
                    id = it.uid,
                    email = it.email!!
                )
            }
        }
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUserStateFlow.update { firebaseAuth.currentUser?.toUser() }
        }
    }

    //Returns the current user as a state flow
    override fun currentUser(): StateFlow<User?> {
        return currentUserStateFlow
    }

    //Tries to create a new user with the passed in email and password if the user does not already exist
    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    //Tries to sign in the user with the corresponding email and password if they exist already
    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    //Signs the current user out
    override fun signOut() {
        return auth.signOut()
    }

    //Deletes the current user if there is one
    override suspend fun delete() {
        if (auth.currentUser != null) {
            auth.currentUser!!.delete()
        }
    }
}
