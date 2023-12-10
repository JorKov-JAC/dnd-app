// Main coding: Jordan

package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.ui.components.monsters.MonstersSearchList
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel
import makovacs.dnd.ui.viewmodels.MonstersViewModel

/**
 * A screen which allows the user to select a [Monster].
 *
 * @param monstersVm The monsters view model to use. Defaults to the currently provided
 * [LocalMonstersViewModel].
 * @param onSelect Called with the monster selected by the user.
 */
@Composable
fun MonstersSelectScreen(
    modifier: Modifier = Modifier,
    monstersVm: MonstersViewModel = LocalMonstersViewModel.current,
    initialQuery: String = "",
    onSelect: (Monster) -> Unit
) {
    val monsters = monstersVm.monsters
    var query by rememberSaveable { mutableStateOf(initialQuery) }

    Column(modifier = modifier) {
        Text(
            "Select a Monster",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(CenterHorizontally)
        )

        if (monsters == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            MonstersSearchList(
                monsters = monsters,
                onClick = onSelect,
                queryStr = query,
                setQueryStr = { query = it }
            )
        }
    }
}
