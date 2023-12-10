package makovacs.dnd.data.dnd.users

import kotlinx.coroutines.flow.StateFlow

/*
 * Main Coding: Makena
 * The interface that auth repository objects can be interacted through
 */

interface AuthRepository {
    fun currentUser(): StateFlow<User?>
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    fun signOut()
    suspend fun delete()
}
