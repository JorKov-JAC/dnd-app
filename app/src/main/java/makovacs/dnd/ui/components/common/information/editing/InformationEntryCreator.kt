// Main coding: Jordan

package makovacs.dnd.ui.components.common.information.editing

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import makovacs.dnd.R
import makovacs.dnd.data.dnd.common.InformationEntry
import makovacs.dnd.data.dnd.common.InformationEntryTypes
import makovacs.dnd.data.dnd.common.Separator
import makovacs.dnd.ui.components.common.dropdowns.StringDropdownSelector

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
