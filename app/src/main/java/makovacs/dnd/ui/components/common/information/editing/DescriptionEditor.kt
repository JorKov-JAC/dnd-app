package makovacs.dnd.ui.components.common.information.editing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.common.Description
import makovacs.dnd.logic.normalizeAndClean

/**
 * UI for editing a [Description].
 *
 * @param initialTitle The initial [Description.title] to edit.
 * @param initialText The initial [Description.text] to edit.
 * @param onSubmit Called with the user's submitted description.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DescriptionEditor(
    modifier: Modifier = Modifier,
    initialTitle: String = "",
    initialText: String = "",
    onSubmit: (Description) -> Unit
) {
    Column(modifier = modifier) {
        var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

        var title by rememberSaveable { mutableStateOf(initialTitle) }
        var text by rememberSaveable { mutableStateOf(initialText) }

        // Title input
        TextField(
            title,
            { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Text (description) input
        TextField(
            text,
            { text = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Error message
        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        // Submit button
        Button({
            try {
                title = title.normalizeAndClean()
                text = text.normalizeAndClean()

                if (title.isBlank() && text.isBlank()) error("Cannot be empty.")

                onSubmit(Description(title.ifBlank { null }, text))
            } catch (ex: Exception) {
                errorMessage = ex.localizedMessage
            }
        }) {
            Text(stringResource(R.string.save))
        }
    }
}
