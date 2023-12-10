package makovacs.dnd.ui.components.common.information

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import makovacs.dnd.data.dnd.common.Description
import makovacs.dnd.data.dnd.common.Header
import makovacs.dnd.data.dnd.common.InformationEntry
import makovacs.dnd.data.dnd.common.Separator

/**
 * Displays an [InformationEntry].
 *
 * @param informationEntry The entry to display.
 */
@Composable
fun InformationEntryDisplay(
    informationEntry: InformationEntry,
    modifier: Modifier = Modifier
) {
    when (informationEntry) {
        is Separator -> SeparatorDisplay(modifier = modifier)
        is Header -> HeaderDisplay(informationEntry, modifier = modifier)
        is Description -> DescriptionDisplay(informationEntry, modifier = modifier)
    }
}
