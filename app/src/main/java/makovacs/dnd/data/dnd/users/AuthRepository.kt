package makovacs.dnd.data.dnd.users

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun currentUser() : StateFlow<User?>
    suspend fun signUp(email: String, password: String) : Boolean
    suspend fun signIn(email: String, password: String): Boolean
    fun signOut()
    suspend fun delete()
}