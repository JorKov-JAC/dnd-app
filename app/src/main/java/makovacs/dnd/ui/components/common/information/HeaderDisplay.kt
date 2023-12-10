// Main coding: Jordan

package makovacs.dnd.ui.components.common.information

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import makovacs.dnd.data.dnd.common.Header

/**
 * Displays a [Header].
 *
 * @param header The header to display.
 */
@Composable
fun HeaderDisplay(header: Header, modifier: Modifier = Modifier) {
    Text(
        header.text,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}
