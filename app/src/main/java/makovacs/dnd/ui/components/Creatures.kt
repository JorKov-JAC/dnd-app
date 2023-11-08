// Contains components related to DnD creatures

package makovacs.dnd.ui.components

import android.graphics.Bitmap
import androidx.annotation.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.ui.util.abilityStrings
import makovacs.dnd.ui.util.toBitmap
import makovacs.dnd.ui.util.toPainter

/**
 * Displays a creature's name.
 *
 * @param name The name to display.
 */
@Composable
fun CreatureName(name: String, modifier: Modifier = Modifier) {
    Text(name, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}

/**
 * Displays a creature's description.
 *
 * @param desc The description to display.
 */
@Composable
fun CreatureDesc(desc: String, modifier: Modifier = Modifier) {
    Text(desc, modifier = modifier)
}

/**
 * Displays a creature's tags.
 *
 * @param tags The tags to display.
 */
@Composable
fun CreatureTags(tags: List<String>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        for (tag in tags) {
            Text(
                tag,
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color(0xFF80FF80))
                    .padding(4.dp, 0.dp)
            )
        }
    }
}

/**
 * Displays a creature's image.
 *
 * @param imageBitmap The bitmap of the image to display.
 * @param imageDesc The optional description of the image. Must be null if
 * [imageBitmap] is null.
 */
@Composable
fun CreatureImage(imageBitmap: Bitmap?, imageDesc: String?, modifier: Modifier = Modifier) {
    // No desc if no image
    assert(if (imageBitmap == null) imageDesc == null else true)

    @Suppress("NAME_SHADOWING")
    val imageDesc = if (imageBitmap == null) {
        stringResource(R.string.noImage)
    } else {
        imageDesc ?: stringResource(R.string.genericMonsterImageDesc)
    }

    Image(
        painter = (imageBitmap ?: R.drawable.no_img.toBitmap()).toPainter(),
        contentDescription = imageDesc,
        modifier = modifier
    )
}

/**
 * Displays information related to ability scores.
 *
 * @param abilityScores The scores to show.
 */
@Composable
fun AbilityScoresDisplay(abilityScores: AbilityScores, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.horizontalScroll(state = scrollState)
    ) {
        for (str in abilityScores.abilityStrings()) {
            Text(str, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp, 0.dp))
        }
    }
}

/**
 * Lets the user edit an [AbilityScores].
 *
 * @param abilityScores The current ability scores. Score may be null if not set.
 * @param setScore Called when the user wants to replace the ability scores at the given index with
 * the provided new score.
 */
@Composable
fun AbilityScoresInput(
    @Size(6) abilityScores: List<Int?>,
    setScore: (index: Int, score: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        val abilityNames = AbilityScores.abilityNames

        for ((i, score) in abilityScores.withIndex()) {
            // Convert sp to dp rather than using dp directly
            // in case user is scaling their text:
            val width = with(LocalDensity.current) { 80.sp.toDp() }

            NullableIntField(
                score,
                { setScore(i, it) },
                label = abilityNames[i],
                modifier = Modifier.width(width)
            )
        }
    }
}
