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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn() {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    Column {
        Button(onClick = {
            navController.navigate(Route.SignUp.route)
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(25.dp))

    Card(modifier = Modifier.padding(15.dp))
    {
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

            }) {
                Text(text = "Sign In")
            }
        }
    }


}



