// Main coding: Jordan

package makovacs.dnd.ui.components.common.information.editing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.common.Description
import makovacs.dnd.data.dnd.common.Header
import makovacs.dnd.data.dnd.common.InformationEntry
import makovacs.dnd.data.dnd.common.Separator
import makovacs.dnd.ui.components.common.information.InformationEntryDisplay
import makovacs.dnd.ui.components.common.lists.EditableList

/**
 * Allows the user to edit a list of [information entries][InformationEntry].
 *
 * @param entries The list of entries.
 * @param swap Called when the user wants to swap two entries.
 * @param insert Called when the user wants to insert the provided entry at the provided index.
 * @param delete Called when the user wants to delete the entry at the provided index. This action
 * should not be confirmed.
 */
@Composable
fun InformationEditor(
    entries: List<InformationEntry>,
    swap: (a: Int, b: Int) -> Unit,
    insert: (index: Int, entry: InformationEntry) -> Unit,
    delete: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = rememberSaveable { mutableStateOf<Int?>(null) }

    EditableList(
        title = "Information",
        items = entries,
        selectedIndexState = selectedIndex,
        onSwap = swap,
        onCreateDialogContent = { insertIndex, onDone ->
            InformationEntryCreator { insert(insertIndex, it); onDone() }
        },
        onEditDialogContent = { itemIndex, item, onDone ->
            val onSubmit: (InformationEntry) -> Unit = {
                // TODO This is O(n), could be O(1), reconsider how swap works and make it
                //      replace with a given object instead
                delete(itemIndex)
                insert(itemIndex, it)
                onDone()
            }

            when (item) {
                is Header -> HeaderEditor(initialText = item.text, onSubmit = onSubmit)
                is Description -> DescriptionEditor(
                    initialTitle = item.title ?: "",
                    initialText = item.text,
                    onSubmit = onSubmit
                )

                is Separator -> error("Not editable, should have been prevented by disabling button.")
            }
        },
        editingEnabled = { _, it -> it !is Separator },
        onDelete = delete,
        itemToName = { it.toString() },
        modifier = Modifier
            .height(400.dp)
            .then(modifier)
    ) {
        when (it) {
            is Separator -> Box(modifier = Modifier.fillMaxWidth()) {
                Text("---- Separator ----", modifier = Modifier.align(Alignment.Center))
            }

            else -> InformationEntryDisplay(it)
        }
    }
}
