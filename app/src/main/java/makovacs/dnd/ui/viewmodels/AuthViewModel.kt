package makovacs.dnd.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.ResultAuth
import makovacs.dnd.data.dnd.users.User
import java.lang.Exception

class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {
    fun currentUser(): StateFlow<User?> {
        return authRepository.currentUser()
    }

    private val _signUpResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signUpResult: StateFlow<ResultAuth<Boolean>?> = _signUpResult

    fun signUp(email: String, password: String) {
        _signUpResult.value = ResultAuth.InProgress
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.signUp(email, password)
                _signUpResult.value = ResultAuth.Success(success)
            } catch (e: FirebaseAuthException) {
                _signUpResult.value = ResultAuth.Failure(e)
            } finally {
                _signInResult.value = ResultAuth.Inactive
                _signOutResult.value = ResultAuth.Inactive
                _deleteResult.value = ResultAuth.Inactive
            }
        }
    }

    private val _signInResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signInResult: StateFlow<ResultAuth<Boolean>?> = _signInResult
    fun signIn(email: String, password: String) {
        _signInResult.value = ResultAuth.InProgress
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.signIn(email, password)
                _signInResult.value = ResultAuth.Success(success)
            } catch (e: FirebaseAuthException) {
                _signInResult.value = ResultAuth.Failure(e)
            } finally {
                _signUpResult.value = ResultAuth.Inactive
                _signOutResult.value = ResultAuth.Inactive
                _deleteResult.value = ResultAuth.Inactive
            }
        }
    }

    private val _signOutResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val signOutResult: StateFlow<ResultAuth<Boolean>?> = _signOutResult
    fun signOut() {
        _signOutResult.value = ResultAuth.InProgress

        try {
            val success = authRepository.signOut()
            _signOutResult.value = ResultAuth.Success(true)
        } catch (e: FirebaseAuthException) {
            _signOutResult.value = ResultAuth.Failure(e)
        } finally {
            _signUpResult.value = ResultAuth.Inactive
            _signInResult.value = ResultAuth.Inactive
            _deleteResult.value = ResultAuth.Inactive
        }
    }

    private val _deleteResult = MutableStateFlow<ResultAuth<Boolean>?>(ResultAuth.Inactive)
    val deleteResult: StateFlow<ResultAuth<Boolean>?> = _deleteResult
    fun delete() {
        _deleteResult.value = ResultAuth.InProgress
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = authRepository.delete()
                _deleteResult.value = ResultAuth.Success(true)
            } catch (e: Exception) {
                _deleteResult.value = ResultAuth.Failure(e)
            } finally {
                _signUpResult.value = ResultAuth.Inactive
                _signInResult.value = ResultAuth.Inactive
                _signOutResult.value = ResultAuth.Inactive
            }
        }
    }
}

class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(MyApp.appModule.authRepository) as T
    }
}
