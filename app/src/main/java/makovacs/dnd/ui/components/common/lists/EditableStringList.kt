// Main coding: Jordan

package makovacs.dnd.ui.components.common.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.logic.swap

/**
 * An [EditableList] which handles a list of strings.
 *
 * @param mutableList The list to use and mutate.
 * @param singleLine True if the strings must be single-line (no line breaks), false otherwise.
 * @param title Optional title to display.
 * @param validator Returns null if the given string is a valid value, or an error message
 * otherwise.
 * @param cleaner Modifies strings before they are added or edited.
 * Occurs before the string is validated and uniqueness is checked.
 * @param uniqueOn If provided, strings will be guaranteed to be unique.
 * This function is called on the strings, and then the results are compared with each other to
 * ensure they are all unique. For example, if this function were [String.lowercase], then "apple"
 * and "Apple" would conflict with each other even if they aren't exactly the same.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableStringList(
    // This component is just a convenience wrapper around EditableList;
    // To keep things simple, we interact with a mutable list directly:
    mutableList: SnapshotStateList<String>,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
    title: String? = null,
    validator: ((String) -> String?)? = null,
    cleaner: ((String) -> String)? = null,
    uniqueOn: ((String) -> Any)? = null
) {
    @Suppress("NAME_SHADOWING")
    val validator = validator ?: { null } // Default to allow everything (null = no errors)
    @Suppress("NAME_SHADOWING")
    val cleaner = cleaner ?: { it } // Default to leaving the string as-is

    val alreadyExistsFormat = stringResource(R.string.already_exists_format)

    EditableList(
        title = title,
        modifier = Modifier
            .height(300.dp)
            .then(modifier),
        items = mutableList,
        key = uniqueOn,
        onSwap = mutableList::swap,
        onCreateDialogContent = { insertionIndex, onDone ->
            var str by rememberSaveable { mutableStateOf("") }
            var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

            Column {
                TextField(str, onValueChange = { str = it }, singleLine = singleLine)

                if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

                Button(onClick = {
                    str = cleaner(str)

                    // Validate the new value
                    errorMsg = validator(str)
                    if (errorMsg != null) return@Button

                    // Ensure uniqueness
                    if (uniqueOn != null) {
                        val comparableStr = uniqueOn(str)
                        val preexisting =
                            mutableList.find { other -> comparableStr == uniqueOn(other) }
                        if (preexisting != null) {
                            // TODO This should probably support a custom ToString function
                            errorMsg = alreadyExistsFormat.format(preexisting)
                            return@Button
                        }
                    }

                    mutableList.add(insertionIndex, str)
                    onDone()
                }) {
                    Text(stringResource(R.string.create))
                }
            }
        },
        onEditDialogContent = { itemIndex, item, onDone ->
            var str by rememberSaveable { mutableStateOf(item) }
            var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

            Column {
                TextField(str, onValueChange = { str = it }, singleLine = singleLine)

                if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

                Button(onClick = {
                    str = cleaner(str)

                    // Validate the new value
                    errorMsg = validator(str)
                    if (errorMsg != null) return@Button

                    // Ensure uniqueness
                    if (uniqueOn != null) {
                        val comparableStr = uniqueOn(str)
                        val preexisting =
                            mutableList.find { other -> comparableStr == uniqueOn(other) }
                        if (
                            preexisting != null &&
                            preexisting != item // Ignore conflicts with itself
                        ) {
                            // TODO This should probably support a custom ToString function
                            errorMsg = alreadyExistsFormat.format(preexisting)
                            return@Button
                        }
                    }

                    mutableList[itemIndex] = str
                    onDone()
                }) {
                    Text(stringResource(R.string.save))
                }
            }
        },
        onDelete = {
            mutableList.removeAt(it)
        },
        itemToName = { it }
    ) {
        Text(it)
    }
}
