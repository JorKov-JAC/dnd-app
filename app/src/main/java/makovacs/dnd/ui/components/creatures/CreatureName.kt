package makovacs.dnd.ui.components.creatures

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Displays a creature's name.
 *
 * @param name The name to display.
 */
@Composable
fun CreatureName(name: String, modifier: Modifier = Modifier) {
    Text(name, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}
