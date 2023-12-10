// Main coding: Makena

package makovacs.dnd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.data.dnd.users.ResultAuth
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.AuthViewModel
import makovacs.dnd.ui.viewmodels.AuthViewModelFactory

/**
 * The screen where users can sign in to an existing account if they have one, or switch to sign up if not
 * @param authViewModel The ViewModel containing the information and logic concerning users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    val userState = authViewModel.currentUser().collectAsState()
    val signInResult by authViewModel.signInResult.collectAsState(ResultAuth.Inactive)
    var message by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Button(onClick = {
            navController.navigate(Route.SignUp.route)
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(25.dp))

        Text(message)

        Card(modifier = Modifier.padding(15.dp)) {
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
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

            )

            Button(modifier = Modifier.padding(15.dp), onClick = {
                authViewModel.signIn(email, password)
                email = ""
                password = ""
            }) {
                Text(text = "Sign In")
            }

            signInResult?.let {
                if (it is ResultAuth.Success && it.data) {
                    navController.navigate(Route.Account.route)
                }
                if (it is ResultAuth.Success || it is ResultAuth.Failure) {
                    message = "Incorrect sign in information."
                }
            }
        }
    }
}
