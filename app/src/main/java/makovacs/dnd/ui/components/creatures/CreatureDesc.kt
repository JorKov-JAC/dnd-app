package makovacs.dnd.ui.components.creatures

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Displays a creature's description.
 *
 * @param desc The description to display.
 */
@Composable
fun CreatureDesc(desc: String, modifier: Modifier = Modifier) {
    Text(desc, modifier = modifier)
}
