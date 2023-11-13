package makovacs.dnd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.data.dnd.users.ResultAuth
import makovacs.dnd.ui.components.InvalidFormInput
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.AuthViewModel
import makovacs.dnd.ui.viewmodels.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())) {
    var message by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    val signUpResult by authViewModel.signUpResult.collectAsState(ResultAuth.Inactive)

    Column {
        Button(onClick = { navController.navigate(Route.SignIn.route) }) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(25.dp))
        Text(message)
        Card(modifier = Modifier.padding(10.dp)) {
            var invalidInput by rememberSaveable { mutableStateOf(false) }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = email,
                singleLine = true,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = password,
                singleLine = true,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            Button(modifier = Modifier.padding(15.dp), onClick = {
                if (isValidEmail(email)) {
                    authViewModel.signUp(email, password)
                } else {
                    invalidInput = true
                }
            }) {
                Text(text = "Sign Up")
            }

            signUpResult?.let {
                if (it is ResultAuth.Success && it.data) {
                    navController.navigate(Route.Account.route)
                }
                if (it is ResultAuth.Success || it is ResultAuth.Failure) {
                    message = "Sign up unsuccessful, please try again."
                }
            }

            if (invalidInput) {
                invalidInput = false
                InvalidFormInput("Invalid email")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    val found = regex.find(email) ?: false

    return true
}
