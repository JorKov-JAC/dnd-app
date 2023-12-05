package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.MonstersSearchList
import makovacs.dnd.ui.components.SignInButtonOrElse
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel

/**
 * Displays the list of [monsters][Monster] from [LocalMonstersViewModel].
 */
@Composable
fun MonstersListScreen(modifier: Modifier = Modifier) {
    val navHostController = LocalNavHostController.current
    val monstersVm = LocalMonstersViewModel.current
    val monsters = monstersVm.monsters

    var query by rememberSaveable { mutableStateOf("") }

    if (monsters == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            // List
            MonstersSearchList(
                monsters = monsters,
                onClick = {
                    navHostController.navigate(Route.MonsterDetailsRoute.go(it.id))
                },
                queryStr = query,
                setQueryStr = { query = it }
            )

            // Create button
            SignInButtonOrElse(
                signInMessage = "Sign In to Add Monsters",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Button({ navHostController.navigate(Route.NewMonsterRoute.route) }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, null /* Described by text */)
                        Text("New", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
