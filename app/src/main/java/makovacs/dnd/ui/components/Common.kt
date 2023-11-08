package makovacs.dnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import makovacs.dnd.R

/**
 * A dialog which displays its contents within a card.
 *
 * @param onDismissRequest Called when the user attempts to dismiss the dialog.
 * @param content The content to display within the dialog.
 */
@Composable
fun CardDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(modifier = modifier) {
            Box(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

/**
 * Shows a confirmation dialog for deleting something.
 *
 * @param onCloseDialog Called when the user attempts to close the dialog, whether that's because
 * they dismissed it or they deleted the item.
 * @param itemStr A string representing the item being deleted, or null to use a
 * generic deletion message.
 * @param onDelete Called when the user confirms the deletion.
 */
@Composable
fun ConfirmDeleteDialog(
    onCloseDialog: ()->Unit,
    itemStr: String?,
    onDelete: ()->Unit
) {
    @Suppress("NAME_SHADOWING")
    val itemStr = if (itemStr == null) {
        stringResource(R.string.confirm_deletion_generic_item_name)
    } else {
        stringResource(R.string.quoted_format, itemStr)
    }

    AlertDialog(
        onDismissRequest = onCloseDialog,
        confirmButton = @Composable {
            Button({
                onDelete()
                onCloseDialog()
            }) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = @Composable {
            Button(onCloseDialog) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = @Composable {
            Icon(Icons.Default.Warning, contentDescription = null /* Decorative */)
        },
        title = @Composable {
            Text(stringResource(R.string.confirm_deletion_title))
        },
        text = @Composable {
            Text(stringResource(R.string.confirm_deletion_body_format, itemStr))
        }
    )
}

/**
 * Displays a list of items and lets the user select one by tapping on it.
 *
 * @param items The items to display.
 * @param selectedIndex The index of the selected item.
 * @param setSelectedIndex Called when the user tries to change the selection to
 * the given index.
 * @param key Converts an item to a unique key.
 * This is necessary if [itemContent] needs to persist state when [items] is modified.
 * @param listState The state of the internal lazy list.
 * @param itemContent The component used to display a given item.
 */
@Composable
fun <T> SelectableList(
    items: List<T>,
    selectedIndex: Int?,
    setSelectedIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    listState: LazyListState = rememberLazyListState(),
    itemContent: @Composable (T) -> Unit
) {
    LaunchedEffect(selectedIndex) {
        // Make sure item is visible
        if (
            selectedIndex != null
                && !listState.layoutInfo.visibleItemsInfo.any { it.index == selectedIndex }
        ) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .then(modifier)
    ) {
        itemsIndexed(items, key = key?.let{ {_, item -> it(item)} }) { index, it ->
            var cardModifier = Modifier.clickable { setSelectedIndex(index) }

            // Change background of selected item
            if (selectedIndex == index) cardModifier = cardModifier
                .background(MaterialTheme.colorScheme.tertiary)

            cardModifier = cardModifier.padding(8.dp)

            OutlinedCard(modifier = cardModifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    itemContent(it)
                }
            }
        }
    }
}
