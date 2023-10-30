package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.MonsterDetails
import makovacs.dnd.ui.routing.LocalNavHostController

/**
 * Shows detailed information about a given monster.
 *
 * @param monster The monster to show information for.
 *
 * If null, will navigate upward; this is useful when the user goes to a monster's page,
 * then goes to the monster list, then deletes the monster, then navigates backward to the
 * individual monster's page again. The monster will no longer exist, so they will navigate further
 * back.
 */
@Composable
fun MonsterDetailsScreen(monster: Monster?, modifier: Modifier = Modifier) {
    val navHostController = LocalNavHostController.current

    if (monster == null) {
        // Probably navigated backward, go further back
        navHostController.navigateUp()
        return
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        MonsterDetails(monster = monster, modifier = Modifier.padding(8.dp))
    }
}