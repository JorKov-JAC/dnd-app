// Contains UI-specific utility code related to DnD

package makovacs.dnd.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import makovacs.dnd.R
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.abilityModifier

/**
 * Creates a formatted string for every ability score.
 */
@Composable
fun AbilityScores.abilityStrings() = (AbilityScores.abilityNames zip scoreList)
    .map { (str, score) ->
        stringResource(
            R.string.ability_name_score_format,
            str,
            score,
            score.abilityModifier
        )
    }

/**
 * Gets localized names of each ability.
 *
 * Ordering is the same as found in the primary constructor.
 */
val AbilityScores.Companion.abilityNames @Composable get() = arrayOf(
    R.string.ability_str,
    R.string.ability_dex,
    R.string.ability_con,
    R.string.ability_int,
    R.string.ability_wis,
    R.string.ability_cha
).map { stringResource(it) }
