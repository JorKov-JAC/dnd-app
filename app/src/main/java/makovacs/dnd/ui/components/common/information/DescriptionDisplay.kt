// Main coding: Jordan

package makovacs.dnd.ui.components.common.information

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import makovacs.dnd.data.dnd.common.Description

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
