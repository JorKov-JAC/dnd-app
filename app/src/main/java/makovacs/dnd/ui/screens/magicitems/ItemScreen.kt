package makovacs.dnd.ui.screens.magicitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.MagicItem
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import makovacs.dnd.ui.components.StringSearchList
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

/*
 * A screen that displays a simple card containing the photo and name of each magic item along that can be clicked on to lead to a more
 * detailed overview of just the clicked on item and allows the user to delete it. Contains a button to remove all the items from the list.
 */
@Composable
fun ItemScreen(magicItems: List<MagicItem>, remove: (String) -> Unit, getByName: (String) -> MagicItem?) {
    val navController = LocalNavHostController.current

    var queryStr by rememberSaveable { mutableStateOf("") }

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
            Row {
                val imageModifier = Modifier
                    .size(80.dp)
                    .border(BorderStroke(1.dp, Color.Black))
                    .background(Color.White)

                Image(
                    painterResource(id = item?.imageId ?: R.drawable.dndmisc),
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
        }
    }

    IconButton(onClick = { navController.navigate(Route.ItemForm.route) }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add magic item")
    }
}
