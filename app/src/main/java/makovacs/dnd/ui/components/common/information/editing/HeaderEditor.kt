// Main coding: Jordan

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
import makovacs.dnd.data.dnd.common.Header
import makovacs.dnd.logic.normalizeAndClean

/**
 * UI for editing a [Header].
 *
 * @param initialText The initial [Header.text] to edit.
 * @param onSubmit Called with the user's submitted header.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderEditor(
    modifier: Modifier = Modifier,
    initialText: String = "",
    onSubmit: (Header) -> Unit
) {
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var text by rememberSaveable { mutableStateOf(initialText) }

    Column(modifier = modifier) {
        // Text (header text) input
        TextField(
            text,
            { text = it },
            label = { Text("Header") },
            singleLine = true,
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
                text = text.normalizeAndClean()

                if (text.isBlank()) error("Cannot be empty.")

                onSubmit(Header(text))
            } catch (ex: Exception) {
                errorMessage = ex.localizedMessage
            }
        }) {
            Text(stringResource(R.string.save))
        }
    }
}
