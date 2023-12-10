// Main coding: Jordan

package makovacs.dnd.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.Description
import makovacs.dnd.data.dnd.Header
import makovacs.dnd.data.dnd.Information
import makovacs.dnd.data.dnd.InformationEntry
import makovacs.dnd.data.dnd.InformationEntryTypes
import makovacs.dnd.data.dnd.Separator
import makovacs.dnd.logic.normalizeAndClean
import makovacs.dnd.ui.components.EditableList
import makovacs.dnd.ui.components.StringDropdownSelector

/**
 * Displays a [Description].
 *
 * @param description The description to display.
 */
@Composable
fun DescriptionDisplay(description: Description, modifier: Modifier = Modifier) {
    Text(description.toAnnotatedString(), modifier = modifier)
}

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

/**
 * Displays a [Separator].
 */
@Composable
fun SeparatorDisplay(modifier: Modifier = Modifier) {
    Divider(modifier = modifier)
}

/**
 * Displays an [InformationEntry].
 *
 * @param informationEntry The entry to display.
 */
@Composable
fun InformationEntryDisplay(
    informationEntry: InformationEntry,
    modifier: Modifier = Modifier
) {
    when (informationEntry) {
        is Separator -> SeparatorDisplay(modifier = modifier)
        is Header -> HeaderDisplay(informationEntry, modifier = modifier)
        is Description -> DescriptionDisplay(informationEntry, modifier = modifier)
    }
}

/**
 * Displays the contents of an [Information].
 *
 * @param information The information to display.
 */
@Composable
fun InformationDisplay(
    information: Information,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        information.entries.forEach { InformationEntryDisplay(it) }
    }
}

/**
 * Allows the user to edit a list of [information entries][InformationEntry].
 *
 * @param entries The list of entries.
 * @param swap Called when the user wants to swap two entries.
 * @param insert Called when the user wants to insert the provided entry at the provided index.
 * @param delete Called when the user wants to delete the entry at the provided index. This action
 * should not be confirmed.
 */
@Composable
fun InformationEditor(
    entries: List<InformationEntry>,
    swap: (a: Int, b: Int) -> Unit,
    insert: (index: Int, entry: InformationEntry) -> Unit,
    delete: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = rememberSaveable { mutableStateOf<Int?>(null) }

    EditableList(
        title = "Information",
        items = entries,
        selectedIndexState = selectedIndex,
        onSwap = swap,
        onCreateDialogContent = { insertIndex, onDone ->
            InformationEntryCreator { insert(insertIndex, it); onDone() }
        },
        onEditDialogContent = { itemIndex, item, onDone ->
            val onSubmit: (InformationEntry) -> Unit = {
                // TODO This is O(n), could be O(1), reconsider how swap works and make it
                //      replace with a given object instead
                delete(itemIndex)
                insert(itemIndex, it)
                onDone()
            }

            when (item) {
                is Header -> HeaderEditor(initialText = item.text, onSubmit = onSubmit)
                is Description -> DescriptionEditor(
                    initialTitle = item.title ?: "",
                    initialText = item.text,
                    onSubmit = onSubmit
                )
                is Separator -> error("Not editable, should have been prevented by disabling button.")
            }
        },
        editingEnabled = { _, it -> it !is Separator },
        onDelete = delete,
        itemToName = { it.toString() },
        modifier = Modifier
            .height(400.dp)
            .then(modifier)
    ) {
        when (it) {
            is Separator -> Box(modifier = Modifier.fillMaxWidth()) {
                Text("---- Separator ----", modifier = Modifier.align(Alignment.Center))
            }
            else -> InformationEntryDisplay(it)
        }
    }
}

/**
 * UI for creating a new [InformationEntry].
 *
 * @param onSubmit Called with the entry which the user has created.
 */
@Composable
fun InformationEntryCreator(
    modifier: Modifier = Modifier,
    onSubmit: (InformationEntry) -> Unit
) {
    Column(modifier = modifier) {
        val types = InformationEntryTypes.values().toList()

        var selectedTypeIndex by rememberSaveable { mutableStateOf<Int?>(null) }

        StringDropdownSelector(
            types,
            value = selectedTypeIndex?.let { types[selectedTypeIndex!!] },
            setValue = { index, _ -> selectedTypeIndex = index },
            reset = null,
            label = "Type"
        )

        if (selectedTypeIndex != null) {
            when (types[selectedTypeIndex!!]) {
                InformationEntryTypes.SEPARATOR -> {
                    Button({
                        onSubmit(Separator)
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }
                InformationEntryTypes.HEADER -> {
                    HeaderEditor(onSubmit = onSubmit)
                }
                InformationEntryTypes.DESCRIPTION -> {
                    DescriptionEditor(onSubmit = onSubmit)
                }
            }
        }
    }
}

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

/**
 * Creates an annotated string for this description.
 */
@Composable
fun Description.toAnnotatedString(): AnnotatedString {
    val description = this

    return buildAnnotatedString {
        if (description.title != null) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("${description.title} ")
            }
        }
        append(description.text)
    }
}
