package makovacs.dnd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    val signUpResult by authViewModel.signUpResult.collectAsState(ResultAuth.Inactive)
    val snackbarHostState = remember { SnackbarHostState() } // Material 3 approach
    Column {
        Button(onClick = { navController.navigate(Route.SignIn.route) })
        {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(25.dp))
    Card(modifier = Modifier.padding(10.dp))
    {

        LaunchedEffect(signUpResult) {
            signUpResult?.let {
                if (it is ResultAuth.Inactive) {
                    return@LaunchedEffect
                }
                if (it is ResultAuth.InProgress) {
                    snackbarHostState.showSnackbar("Sign-up In Progress")
                    return@LaunchedEffect
                }
                if (it is ResultAuth.Success && it.data) {
                    snackbarHostState.showSnackbar("Sign-up Successful")
                } else if (it is ResultAuth.Failure || it is ResultAuth.Success) { // success(false) case
                    snackbarHostState.showSnackbar("Sign-up Unsuccessful")
                }
            }
        }

        var invalidInput by rememberSaveable { mutableStateOf(false) }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = username,
                singleLine = true,
                onValueChange = { username = it },
                label = { Text("Username") })
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = email,
                singleLine = true,
                onValueChange = { email = it },
                label = { Text("Email") })
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = password,
                singleLine = true,
                onValueChange = { password = it },
                label = { Text("Password") })


            Button(modifier = Modifier.padding(15.dp), onClick = {
                if(isValidEmail(email) && isValidUsername(username))
                {
                    authViewModel.signUp(email, password)
                }
                else
                {
                    invalidInput = true
                }

            }) {
                Text(text = "Sign Up")
            }

        if (invalidInput) {
            invalidInput = false
            InvalidFormInput("Invalid user input")
        }


        }

        Spacer(modifier = Modifier.height(20.dp))
        

    }


}

fun isValidEmail(email: String) : Boolean
{
    val regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    val found = regex.find(email) ?: false

    return true
}

fun isValidUsername(username: String) : Boolean
{
    val regex = "^[a-zA-Z]*$".toRegex()
    val found = regex.find(username) ?: false

    return true
}
