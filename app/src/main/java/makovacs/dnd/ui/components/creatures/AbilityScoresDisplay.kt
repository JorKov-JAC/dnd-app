package makovacs.dnd.ui.components.creatures

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import makovacs.dnd.data.dnd.creatures.AbilityScores
import makovacs.dnd.ui.components.common.EvenWidthGrid
import makovacs.dnd.ui.util.abilityAnnotatedStrings

/**
 * Displays information related to ability scores.
 *
 * @param abilityScores The scores to show.
 */
@Composable
fun AbilityScoresDisplay(abilityScores: AbilityScores, modifier: Modifier = Modifier) {
    EvenWidthGrid(modifier = modifier) {
        for (str in abilityScores.abilityAnnotatedStrings()) {
            Text(
                str,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
