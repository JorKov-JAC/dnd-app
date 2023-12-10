// Main coding: Makena

package makovacs.dnd.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.ResultAuth
import makovacs.dnd.data.dnd.users.User
import java.lang.Exception

/**
 * The ViewModel containing the information and logic concerning users
 * @property authRepository The connection to the repository for the users
 */
class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {
    // gets the current user
    fun currentUser(): StateFlow<User?> {
        return authRepository.currentUser()
    }

    private val _signUpResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signUpResult: StateFlow<ResultAuth<Boolean>?> = _signUpResult

    // Sends the user's information to the repository to try to sign them up
    fun signUp(email: String, password: String) {
        _signUpResult.update { ResultAuth.InProgress }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.signUp(email, password)
                _signUpResult.update { ResultAuth.Success(success) }
            } catch (e: FirebaseAuthException) {
                _signUpResult.update { ResultAuth.Failure(e) }
            } finally {
                _signInResult.update { ResultAuth.Inactive }
                _signOutResult.update { ResultAuth.Inactive }
                _deleteResult.update { ResultAuth.Inactive }
            }
        }
    }

    private val _signInResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signInResult: StateFlow<ResultAuth<Boolean>?> = _signInResult

    // Sends the user's information to the repository to try to sign them in
    fun signIn(email: String, password: String) {
        _signInResult.update { ResultAuth.InProgress }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.signIn(email, password)
                _signInResult.update { ResultAuth.Success(success) }
            } catch (e: FirebaseAuthException) {
                _signInResult.update { ResultAuth.Failure(e) }
            } finally {
                _signUpResult.update { ResultAuth.Inactive }
                _signOutResult.update { ResultAuth.Inactive }
                _deleteResult.update { ResultAuth.Inactive }
            }
        }
    }

    private val _signOutResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signOutResult: StateFlow<ResultAuth<Boolean>?> = _signOutResult

    // Sends the user's information to the repository to try to sign them out
    fun signOut() {
        _signOutResult.update { ResultAuth.InProgress }

        try {
            val success = authRepository.signOut()
            _signOutResult.update { ResultAuth.Success(true) }
        } catch (e: FirebaseAuthException) {
            _signOutResult.update { ResultAuth.Failure(e) }
        } finally {
            _signUpResult.update { ResultAuth.Inactive }
            _signInResult.update { ResultAuth.Inactive }
            _deleteResult.update { ResultAuth.Inactive }
        }
    }

    private val _deleteResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val deleteResult: StateFlow<ResultAuth<Boolean>?> = _deleteResult

    // Sends the user's information to the repository to delete their account
    fun delete() {
        _deleteResult.update { ResultAuth.InProgress }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.delete()
                _deleteResult.update { ResultAuth.Success(true) }
            } catch (e: Exception) {
                _deleteResult.update { ResultAuth.Failure(e) }
            } finally {
                _signUpResult.update { ResultAuth.Inactive }
                _signInResult.update { ResultAuth.Inactive }
                _signOutResult.update { ResultAuth.Inactive }
            }
        }
    }
}

class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(MyApp.appModule.authRepository) as T
    }
}
