package makovacs.dnd.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.User

class AuthViewModel(val authRepository: AuthRepository) : ViewModel()
{
    fun currentUser(): StateFlow<User?> {
        return authRepository.currentUser()
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signUp(email, password)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signIn(email, password)
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun delete(){
        viewModelScope.launch {
            authRepository.delete()
        }
    }
}

class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //figure out what MyApp is
        return AuthViewModel(MyApp.appModule.authRepository) as T
    }
}

