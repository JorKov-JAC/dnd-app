// Main coding: Jordan

package makovacs.dnd.ui.components.common.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.ui.components.common.CardDialog
import makovacs.dnd.ui.components.common.ConfirmDeleteDialog

/**
 * Displays a list and allows the user to modify it.
 *
 * @param items The list of items to display.
 * @param onSwap Called when the user swaps two items.
 * @param key Converts an item to a unique key.
 * This is necessary if [itemContent] needs to persist state when [items] is modified.
 * @param selectedIndexState A state representing the currently selected item, or null if there is
 * no selected item.
 * @param onCreateDialogContent Component for creating a new item,
 * or null if the user cannot create items.
 * Is given the index at which the new item should be inserted.
 * Should call onDone when an item has finished being added.
 * @param onDelete Called when the user tries to delete an item with the given index,
 * or null if the user cannot delete items.
 * @param itemToName Gets an item's user-visible name.
 * @param onEditDialogContent Component for editing an item,
 * or null if the user cannot edit items.
 * Is given the item and its index.
 * Should call onDone when an item has finished being edited.
 * @param editingEnabled Called with the currently selected item and its index.
 * If it returns true and [onEditDialogContent] is not null, the item can be editing.
 * Defaults to always be true.
 * @param title Optional title to display.
 * @param itemContent Converts an item into a component.
 */
@Composable
fun <T> EditableList(
    items: List<T>,
    onSwap: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    // The only way for callers to *optionally* interact with the selected index is to expose a
    // state for it. Having a value and a setter instead would force the caller to provide them:
    selectedIndexState: MutableState<Int?> = rememberSaveable {
        mutableStateOf(
            null
        )
    },
    onCreateDialogContent: (@Composable (insertionIndex: Int, onDone: () -> Unit) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    itemToName: ((T) -> String)? = null,
    onEditDialogContent: (@Composable (itemIndex: Int, item: T, onDone: () -> Unit) -> Unit)? = null,
    editingEnabled: (index: Int, item: T) -> Boolean = { _, _ -> true },
    title: String? = null,
    itemContent: @Composable (T) -> Unit
) {
    var selectedIndex by selectedIndexState

    Column(modifier = modifier) {
        // Header
        // TODO This condition is fragile and may break if we forget to update
        //      it when adding new header items, replace with something cleaner:
        if (title != null || onCreateDialogContent != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            ) {
                // Title
                if (title != null) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // "Create" button
                if (onCreateDialogContent != null) {
                    var createDialogEnabled by rememberSaveable {
                        mutableStateOf(
                            false
                        )
                    }
                    IconButton(onClick = { createDialogEnabled = true }) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(R.string.create),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    if (createDialogEnabled) {
                        CardDialog(
                            onDismissRequest = { createDialogEnabled = false }
                        ) {
                            val insertionIndex =
                                selectedIndex?.let { it + 1 } ?: items.size

                            onCreateDialogContent(insertionIndex) {
                                createDialogEnabled = false
                                selectedIndex = insertionIndex
                            }
                        }
                    }
                }
            }
        }

        // Display items
        SelectableList(
            items = items,
            key = key,
            selectedIndex = selectedIndex,
            setSelectedIndex = { selectedIndex = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            itemContent = itemContent
        )

        // Mutation buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .fillMaxWidth()
        ) {
            // Up button
            IconButton(
                enabled = selectedIndex?.let { it > 0 } ?: false,
                onClick = {
                    onSwap(selectedIndex!!, selectedIndex!! - 1)
                    selectedIndex = selectedIndex!! - 1
                }
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    stringResource(R.string.move_up)
                )
            }

            // Down button
            IconButton(
                enabled = selectedIndex?.let { it < items.size - 1 } ?: false,
                onClick = {
                    onSwap(selectedIndex!!, selectedIndex!! + 1)
                    selectedIndex = selectedIndex!! + 1
                }
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    stringResource(R.string.move_down)
                )
            }

            // Edit button
            if (onEditDialogContent != null) {
                var editDialogEnabled by rememberSaveable { mutableStateOf(false) }
                IconButton(
                    enabled = selectedIndex != null &&
                        editingEnabled(selectedIndex!!, items[selectedIndex!!]),
                    onClick = { editDialogEnabled = true }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        stringResource(R.string.edit)
                    )
                }

                if (editDialogEnabled) {
                    CardDialog(
                        onDismissRequest = { editDialogEnabled = false }
                    ) {
                        onEditDialogContent(
                            selectedIndex!!,
                            items[selectedIndex!!]
                        ) { editDialogEnabled = false }
                    }
                }
            }

            // Delete button
            // TODO Confirmation prompt
            if (onDelete != null) {
                var deleteConfirmationVisible by rememberSaveable { mutableStateOf(false) }

                IconButton(
                    enabled = selectedIndex != null,
                    onClick = {
                        deleteConfirmationVisible = true
                    }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        stringResource(R.string.delete)
                    )
                }

                if (deleteConfirmationVisible) {
                    ConfirmDeleteDialog(
                        onCloseDialog = { deleteConfirmationVisible = false },
                        itemStr = itemToName?.invoke(items[selectedIndex!!])
                    ) {
                        onDelete(selectedIndex!!)
                        selectedIndex = null
                    }
                }
            }
        }
    }
}
