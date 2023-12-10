// Main coding: Jordan

package makovacs.dnd.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

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
