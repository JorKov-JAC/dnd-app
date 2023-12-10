// Main coding: Jordan

package makovacs.dnd.ui.components.common.dropdowns

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle

/**
 * Allows the user to select an item using a dropdown.
 *
 * @param choices The list of selectable values.
 * @param setValue Called when the user selects the given choice.
 * @param reset Provides a "reset" option to the user to reset the selection. If null, no such
 * option is given.
 * @param choiceName Converts an item from [choices] into a displayable name.
 * @param choiceIcon Converts an item from [choices] into an icon. If null, no icon is shown.
 * @param content The content which can be clicked to open the dropdown.
 */
@Composable
fun <T : Any> DropdownSelector(
    choices: Iterable<T>,
    setValue: (index: Int, value: T) -> Unit,
    reset: (() -> Unit)?,
    choiceName: (T) -> String,
    modifier: Modifier = Modifier,
    choiceIcon: (@Composable (T) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.clickable { dropdownExpanded = !dropdownExpanded }) {
        content()

        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            if (reset != null) {
                DropdownMenuItem(
                    text = { Text("Reset", fontStyle = FontStyle.Italic) },
                    onClick = {
                        reset()
                        dropdownExpanded = false
                    }
                )
            }

            choices.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(choiceName(item)) },
                    onClick = {
                        setValue(index, item)
                        dropdownExpanded = false
                    },
                    trailingIcon = choiceIcon?.let { { choiceIcon(item) } }
                )
            }
        }
    }
}
