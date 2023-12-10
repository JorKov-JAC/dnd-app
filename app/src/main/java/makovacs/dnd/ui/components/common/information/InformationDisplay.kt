package makovacs.dnd.ui.components.common.information

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.common.Information

/**
 * Displays the contents of an [Information].
 *
 * @param information The information to display.
 */
@Composable
fun InformationDisplay(
    information: Information,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        information.entries.forEach { InformationEntryDisplay(it) }
    }
}
