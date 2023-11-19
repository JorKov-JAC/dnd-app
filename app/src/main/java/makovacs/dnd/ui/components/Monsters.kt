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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery
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

            // Name and description
            CreatureName(name = monster.name)
            CreatureDesc(desc = monster.descriptionOrDefault)

            // Tags
            if (monster.tags.isNotEmpty()) {
                Divider()
                CreatureTags(tags = monster.tags)
            }

            Divider()

            // AC and HP
            CreatureArmorAndHp(
                ac = monster.armorClass,
                hitDice = monster.hitDice,
                averageHp = monster.avgHitPoints
            )

            Divider()

            // Ability scores
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

    val imageOfFormat = stringResource(R.string.imageOf_format)

    Card(modifier = modifier) {
        DropdownSelector(
            listOf(
                R.drawable.gnolls.toBitmap() to stringResource(id = R.string.gnolls),
                R.drawable.wolf.toBitmap() to stringResource(id = R.string.wolf),
                R.drawable.imp.toBitmap() to stringResource(id = R.string.imp)
            ),
            setValue = { _, it -> setBitmap(it.first, imageOfFormat.format(it.second)) },
            reset = { setBitmap(null, null) },
            choiceName = { it.second },
            choiceIcon = {
                Image(
                    it.first.toPainter(),
                    imageOfFormat.format(it.second),
                    modifier = Modifier.sizeIn(maxWidth = 150.dp, maxHeight = 150.dp)
                )
            },
            modifier = Modifier.padding(8.dp)
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
        }
    }
}

/**
 * Displays a list of Monsters.
 *
 * @param monsters The monsters to display.
 * @param onClick Called when the user clicks on a Monster. Null if nothing should happen.
 * @param onDelete Called when the user tries to delete a Monster. Null if user cannot delete
 * monsters.
 * @param queryStr The string to use as a search query.
 * @param setQueryStr Called when the user tries to set [queryStr] to the provided value.
 */
@Composable
fun MonstersSearchList(
    monsters: List<Monster>,
    onClick: ((Monster) -> Unit)?,
    onDelete: ((Monster) -> Unit)?,
    queryStr: String,
    setQueryStr: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StringSearchList(
        items = monsters,
        queryStr = queryStr,
        setQueryStr = setQueryStr,
        queryModifier = MonsterQuery.Companion::fromString,
        mapper = { query, monster -> if (query.matches(monster)) monster.name else null },
        label = "Query (ex: \"Gnoll +Humanoid -Small\")",
        key = { it.id },
        modifier = Modifier
            .padding(4.dp)
            .then(modifier)
    ) { _, it ->
        Box {
            var cardModifier: Modifier = Modifier

            // Add clicking if a callback was provided
            // (can't use "enabled" for accessibility reasons)
            if (onClick != null) cardModifier = cardModifier.clickable { onClick(it) }

            MonsterCard(it, cardModifier)

            // Add delete button if a callback was provided
            if (onDelete != null) {
                IconButton(
                    onClick = { onDelete(it) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, "Delete \"${it.name}\"")
                }
            }
        }
    }
}
