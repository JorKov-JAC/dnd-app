package makovacs.dnd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.data.dnd.users.ResultAuth
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.AuthViewModel
import makovacs.dnd.ui.viewmodels.AuthViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(authViewModel: AuthViewModel = viewModel(factory=AuthViewModelFactory())) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    val userState = authViewModel.currentUser().collectAsState()
    Column {
        Button(onClick = {
            navController.navigate(Route.SignUp.route)
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(25.dp))

        Card(modifier = Modifier.padding(15.dp))
        {

            if(userState.value == null) {
                Text("Not logged in")
            }
            else {
                Text("logged in")
            }



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
                value = password,
                singleLine = true,
                onValueChange = { password = it },
                label = { Text("Password") })

            Button(modifier = Modifier.padding(15.dp), onClick = {
                authViewModel.signIn(username, password)
                username = ""
                password = ""
            }) {
                Text(text = "Sign In")
            }
        }
    }


}



