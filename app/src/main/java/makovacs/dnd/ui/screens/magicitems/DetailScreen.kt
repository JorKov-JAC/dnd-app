package makovacs.dnd.ui.screens.magicitems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.MagicItems

/*
    A screen showing a large formatted version of the passed in magic item string.
 */
@Composable
fun DetailScreen(items: String) {
    val magicList = MagicItems.current
    val info = items.split("_")
    Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var name = info[4]

            val context = LocalContext.current
            val drawableId = remember(name) {
                context.resources.getIdentifier(
                    name,
                    "drawable",
                    context.packageName
                )
            }
            Image(
                painterResource(id = drawableId),
                contentDescription = "Magic item",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(5.dp)
            )
            Text(
                "Name: ${info[0]} \nSource: ${info[1]} \nRarity: ${info[2]}, \nDescription: ${info[3]}",
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }

    Button(onClick = {
        try {
            magicList.remove(items)
        } catch (err: Throwable) {
        }
    }) {
        Text("Remove item")
    }
}
