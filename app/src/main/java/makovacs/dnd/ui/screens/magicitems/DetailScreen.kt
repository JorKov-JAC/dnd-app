// Main coding: Makena

package makovacs.dnd.ui.screens.magicitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.magicItems.MagicItem

/**
 * A screen showing a large formatted version of the passed in magic item string.
 * @param name the name of the magic item
 * @param remove the function to remove the item based on it's name
 * @param getByName the function to get the details of the magic item from it's name
 */
@Composable
fun DetailScreen(name: String, remove: (String) -> Unit, getByName: (String) -> MagicItem?) {
    var item = getByName(name)
    val context = LocalContext.current

    Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var id = context.resources.getIdentifier(
                item?.image ?: "dndmisc",
                "drawable",
                "makovacs.dnd"
            )
            Image(
                painterResource(id = id),
                contentDescription = "Magic item",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(5.dp)
            )
            Text(
                item?.toString() ?: "No item found",
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }

    Button(onClick = {
        try {
            remove(name)
        } catch (err: Throwable) {
        }
    }) {
        Text("Remove item")
    }
}
