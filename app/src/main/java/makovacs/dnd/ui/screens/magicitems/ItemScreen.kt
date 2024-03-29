// Main coding: Makena

package makovacs.dnd.ui.screens.magicitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.data.dnd.magicItems.MagicItem
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import makovacs.dnd.ui.components.common.ForwardArrow
import makovacs.dnd.ui.components.common.SignInButtonOrElse
import makovacs.dnd.ui.components.common.search.StringSearchList
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.AuthViewModel
import makovacs.dnd.ui.viewmodels.AuthViewModelFactory

/**
 * A screen that displays a simple card containing the photo and name of each magic item along that can be clicked on to lead to a more
 * detailed overview of just the clicked on item and allows the user to delete it. Contains a button to remove all the items from the list.
 * @param magicItems The list of the magic items
 * @param remove The function to remove the magic item based on it's name
 * @param getByName The function to retrieve the details of a magic item based on it's name
 * @param getAllItems The function to get all the items from the database
 * @param authViewModel The viewmodel containing the information and logic concerning users
 */
@Composable
fun ItemScreen(magicItems: List<MagicItem>, remove: (String) -> Unit, getByName: (String) -> MagicItem?, getAllItems: () -> Unit, authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())) {
    val navController = LocalNavHostController.current
    val userState = authViewModel.currentUser().collectAsState()
    var queryStr by rememberSaveable { mutableStateOf("") }
    getAllItems()
    var triedAddingItemWhileLoggedOut by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    StringSearchList(
        items = magicItems,
        queryStr = queryStr,
        setQueryStr = { queryStr = it },
        queryModifier = String::normalizeForInsensitiveComparisons,
        mapper = { query, item -> if (item.name.normalizeForInsensitiveComparisons().contains(query)) item.name else null },
        key = /* TODO */ null,
        label = "Item Name",
        modifier = Modifier.padding(5.dp)
    ) { _, item ->
        Card(
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.clickable {
                navController.navigate(Route.SingleItem.go(item.name))
            }
        ) {
            Box {
                Row {
                    val imageModifier = Modifier
                        .size(80.dp)
                        .border(BorderStroke(1.dp, Color.Black))
                        .background(Color.White)
                    var id = context.resources.getIdentifier(
                        item?.image ?: "R.drawable.dndmisc",
                        "drawable",
                        "makovacs.dnd"
                    )
                    Image(
                        painterResource(id = id),
                        contentDescription = "...",
                        modifier = imageModifier
                    )
                    Text(
                        "Name: ${item.name}",
                        Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )
                }

                ForwardArrow(modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // New entry button
        SignInButtonOrElse(
            signInMessage = "Sign In to Access Items",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Button({ navController.navigate(Route.ItemForm.route) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, null /* Described by text */)
                    Text("New", textAlign = TextAlign.Center)
                }
            }
        }
    }

    if (triedAddingItemWhileLoggedOut) {
        triedAddingItemWhileLoggedOut = false
        makovacs.dnd.ui.components.common.InvalidFormInput("Please sign in to create items.")
    }
}
