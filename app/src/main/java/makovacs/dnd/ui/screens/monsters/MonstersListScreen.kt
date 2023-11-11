package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import makovacs.dnd.ui.components.MonsterCard
import makovacs.dnd.ui.components.StringSearchList
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

    Box(modifier = modifier.fillMaxSize()) {
        MonstersList(
            monsters = monstersVm.monsters,
            onClick = {
                navHostController.navigate(Route.MonsterDetailsRoute.go(it.name))
            },
            onDelete = { monstersVm.removeMonster(it.name) }
        )
        Button(
            onClick = { navHostController.navigate(Route.NewMonsterRoute.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Text("+")
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
 */
@Composable
fun MonstersList(
    monsters: List<Monster>,
    onClick: ((Monster) -> Unit)?,
    onDelete: ((Monster) -> Unit)?,
    modifier: Modifier = Modifier
) {
    StringSearchList(
        items = monsters,
        queryModifier = { it.normalizeForInsensitiveComparisons() },
        mapper = { query, item -> if (item.name.normalizeForInsensitiveComparisons().contains(query)) item.name else null },
        key = { it.id },
        modifier = Modifier.padding(4.dp).then(modifier)
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
