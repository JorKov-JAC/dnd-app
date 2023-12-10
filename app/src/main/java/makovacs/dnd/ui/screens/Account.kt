// Main coding: Makena

package makovacs.dnd.ui.screens

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.AuthViewModel
import makovacs.dnd.ui.viewmodels.AuthViewModelFactory

/**
 * The account page of a user
 *
 * @param deleteAll Deletes all the magic items belonging to a user (when they delete their account)
 * @param authViewModel The ViewModel containing the information and logic concerning users
 */
@Composable
fun Account(deleteAll: () -> Unit, authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val navController = LocalNavHostController.current
    val userState = authViewModel.currentUser().collectAsState()

    Column {
        if (userState.value == null) {
            navController.navigate(Route.SignIn.route)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Card(modifier = Modifier.padding(30.dp).width(500.dp)) {
            Text("Signed in", Modifier.padding(10.dp).align(Alignment.CenterHorizontally), fontSize = 25.sp)

            Button(onClick = {
                authViewModel.signOut()
            }, Modifier.padding(5.dp).width(400.dp)) {
                Text("Sign Out")
            }

            val context = LocalContext.current
            Button(onClick = {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Account Deletion")
                builder.setMessage("Are you sure you want to delete your account")
                builder.setPositiveButton("Confirm") { _, _ ->
                    deleteAll()
                    authViewModel.delete()
                }
                builder.setNeutralButton("Close") { _, _ -> }

                builder.show()
            }, Modifier.padding(5.dp).width(400.dp)) {
                Text(text = "Delete Account")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
