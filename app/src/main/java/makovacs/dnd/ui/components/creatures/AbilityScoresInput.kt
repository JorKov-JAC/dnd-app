// Main coding: Jordan
// Contains components related to DnD creatures

package makovacs.dnd.ui.components.creatures

import androidx.annotation.Size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import makovacs.dnd.data.dnd.creatures.AbilityScores
import makovacs.dnd.ui.components.common.EvenWidthGrid
import makovacs.dnd.ui.components.common.NullableIntField

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
    EvenWidthGrid(modifier = modifier) {
        val abilityNames = AbilityScores.abilityNames

        for ((i, score) in abilityScores.withIndex()) {
            // Convert sp to dp rather than using dp directly
            // in case user is scaling their text:
            val width = with(LocalDensity.current) { 80.sp.toDp() }

            NullableIntField(
                score,
                { setScore(i, it) },
                label = abilityNames[i],
                modifier = Modifier
                    .width(width)
                    .padding(4.dp)
            )
        }
    }
}
