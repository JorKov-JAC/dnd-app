package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.MonstersSearchList
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

    var query by rememberSaveable { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        MonstersSearchList(
            monsters = monstersVm.monsters,
            onClick = {
                navHostController.navigate(Route.MonsterDetailsRoute.go(it.name))
            },
            onDelete = { monstersVm.removeMonster(it.name) },
            queryStr = query,
            setQueryStr = { query = it }
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
