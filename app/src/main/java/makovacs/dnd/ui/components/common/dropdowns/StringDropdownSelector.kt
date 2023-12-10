package makovacs.dnd.ui.components.common.dropdowns

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A [DropdownSelector] for items which are displayed to the user as strings.
 *
 * @param choices The list of selectable values.
 * @param value The currently selected value.
 * @param setValue Called when the user selects the given choice.
 * @param reset Provides a "reset" option to the user to reset the selection. If null, no such
 * option is given.
 * @param label A label to put before the selector.
 * @param choiceName Converts an item from [choices] into a displayable name.
 * If null, defaults to [Any.toString].
 */
@Composable
fun <T : Any> StringDropdownSelector(
    choices: Iterable<T>,
    value: T?,
    setValue: (index: Int, value: T) -> Unit,
    reset: (() -> Unit)?,
    label: String?,
    modifier: Modifier = Modifier,
    choiceName: (T) -> String = { it.toString() }
) {
    Row(modifier = modifier) {
        if (label != null) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        DropdownSelector(
            choices = choices,
            setValue = setValue,
            reset = reset,
            choiceName = choiceName,
            modifier = Modifier.weight(1f)
        ) {
            Card {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        value?.let { choiceName(it) } ?: "",
                        modifier = Modifier.widthIn(min = 32.dp).weight(1f)
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null /* Decorative */,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
