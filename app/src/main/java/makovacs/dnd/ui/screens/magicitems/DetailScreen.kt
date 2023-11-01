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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.MagicItem

/*
    A screen showing a large formatted version of the passed in magic item string.
 */
@Composable
fun DetailScreen(name: String, remove: (String) -> Unit, getByName: (String) -> MagicItem?)
{
    var item = getByName(name)
    Card(border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary), colors = CardDefaults.cardColors(
        MaterialTheme.colorScheme.tertiaryContainer)){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            var id = item?.imageId ?: R.drawable.dndmisc
            Image(
                painterResource(id = id),
                contentDescription = "Magic item",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(5.dp)
            )
            Text(item?.toString() ?: "No item found",
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth())

        }

    }


    Button(onClick = {
        try{
            remove(name)
        }
        catch(err: Throwable)
        {

        }
    }){
        Text("Remove item")
    }

}
