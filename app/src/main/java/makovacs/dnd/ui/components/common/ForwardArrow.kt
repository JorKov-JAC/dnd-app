// Main coding: Jordan

package makovacs.dnd.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A subtle arrow which indicates that something can be clicked.
 */
@Composable
fun ForwardArrow(modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.ArrowForward,
        null, // Decorative
        tint = MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .padding(16.dp)
            .then(modifier)
    )
}
