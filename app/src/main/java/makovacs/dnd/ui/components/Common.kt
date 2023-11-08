package makovacs.dnd.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import makovacs.dnd.R

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
