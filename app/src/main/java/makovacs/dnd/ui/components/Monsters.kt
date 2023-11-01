// Contains components specific to DnD monsters (which are a subcategory of creatures)

package makovacs.dnd.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.util.toBitmap
import makovacs.dnd.ui.util.toPainter

/**
 * Displays a summary of a monster's information and stats.
 *
 * @param monster The monster whose stats should be shown.
 */
@Composable
fun MonsterCard(monster: Monster, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp)) {
        Row {
            CreatureImage(
                monster.imageBitmap,
                monster.imageDesc,
                modifier = Modifier
                    .sizeIn(maxWidth = 100.dp, maxHeight = 100.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                CreatureName(name = monster.name)
                Divider()
                CreatureTags(tags = monster.tags)
            }
        }
    }
}

/**
 * Displays detailed information for a monster.
 *
 * @param monster The monster to show information of.
 */
@Composable
fun MonsterDetails(monster: Monster, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Image
            CreatureImage(
                imageBitmap = monster.imageBitmap,
                imageDesc = monster.imageDesc,
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(maxWidth = 150.dp, maxHeight = 150.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Details
            CreatureName(name = monster.name)
            CreatureDesc(desc = monster.descriptionOrDefault)

            Divider()

            CreatureTags(tags = monster.tags)

            Divider()

            AbilityScoresDisplay(
                abilityScores = monster.abilityScores,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Lets the user select a bitmap for a monster.
 *
 * @param bitmap The current bitmap, or null if there is none.
 * @param setBitmap Called when the user selects a bitmap.
 * Is passed in the bitmap image as the first argument or null if the user chooses to have no
 * bitmap, and an optional description of the image as the second argument.
 */
@Composable
fun MonsterBitmapSelector(bitmap: Bitmap?, setBitmap: (Bitmap?, String?) -> Unit, modifier: Modifier = Modifier) {
    // TODO This is gross, decodes image on every recompose:
    val defaultBitmap = R.drawable.no_img.toBitmap()

    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    fun setBitmapAndCloseDropdown(bitmap: Bitmap?, bitmapDesc: String?) {
        setBitmap(bitmap, bitmapDesc)
        dropdownExpanded = false
    }

    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .clickable { dropdownExpanded = !dropdownExpanded }
        ) {
            Image(
                painter = (bitmap ?: defaultBitmap).toPainter(),
                contentDescription = null // Useless unless we implement user-provided descriptions
            )

            Icon(
                Icons.Default.ArrowDropDown,
                stringResource(id = R.string.newMonster_imageDropdownIcon),
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("None") },
                    onClick = { setBitmapAndCloseDropdown(null, null) }
                )
                listOf(
                    stringResource(id = R.string.gnolls) to R.drawable.gnolls.toBitmap(),
                    stringResource(id = R.string.wolf) to R.drawable.wolf.toBitmap(),
                    stringResource(id = R.string.imp) to R.drawable.imp.toBitmap()
                ).forEach { (itemName, itemBitmap) ->
                    val bitmapDesc = stringResource(R.string.imageOf_format, itemName)
                    DropdownMenuItem(
                        text = { Text(itemName) },
                        onClick = { setBitmapAndCloseDropdown(itemBitmap, bitmapDesc) },
                        trailingIcon = {
                            Image(
                                itemBitmap.toPainter(),
                                bitmapDesc,
                                modifier = Modifier.sizeIn(maxWidth = 150.dp, maxHeight = 150.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}