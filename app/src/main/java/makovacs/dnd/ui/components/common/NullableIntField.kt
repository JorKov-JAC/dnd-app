// Main coding: Jordan

package makovacs.dnd.ui.components.common

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * An input field for ints that can be empty.
 *
 * @param value The current value.
 * @param setValue Called when the user wants to change [value] to the provided value.
 * @param label The label shown on the input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NullableIntField(
    value: Int?,
    setValue: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value?.toString() ?: "",
        {
            if (it.isBlank()) {
                setValue(null)
            } else {
                it.toIntOrNull()?.let(setValue)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}
