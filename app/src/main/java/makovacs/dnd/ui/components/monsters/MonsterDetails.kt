package makovacs.dnd.ui.components.monsters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.ui.components.common.EvenWidthGrid
import makovacs.dnd.ui.components.common.information.InformationDisplay
import makovacs.dnd.ui.components.creatures.AbilityScoresDisplay
import makovacs.dnd.ui.components.creatures.CreatureArmorAndHp
import makovacs.dnd.ui.components.creatures.CreatureDesc
import makovacs.dnd.ui.components.creatures.CreatureImage
import makovacs.dnd.ui.components.creatures.CreatureName
import makovacs.dnd.ui.components.creatures.CreatureTags

/**
 * Displays detailed information for a monster.
 *
 * @param monster The monster to show information of.
 */
@Composable
fun MonsterDetails(monster: Monster, modifier: Modifier = Modifier) {
    /** [Divider] component with vertical padding. */
    val PaddedDivider = @Composable { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Image
            CreatureImage(
                imageBitmap = monster.imageBitmap,
                imageDesc = monster.imageDesc,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Name and description
            CreatureName(name = monster.name)
            CreatureDesc(desc = monster.descriptionOrDefault)

            // Tags
            if (monster.tags.isNotEmpty()) {
                PaddedDivider()
                CreatureTags(tags = monster.tags)
            }

            PaddedDivider()

            // AC and HP
            CreatureArmorAndHp(
                ac = monster.armorClass,
                hitDice = monster.hitDice,
                averageHp = monster.avgHitPoints
            )

            PaddedDivider()

            // Ability scores
            AbilityScoresDisplay(
                abilityScores = monster.abilityScores,
                modifier = Modifier.fillMaxWidth()
            )

            PaddedDivider()

            // Misc Stats
            val MiscFactText: @Composable (title: String, text: String) -> Unit = { title, text ->
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("$title: ")
                        }
                        append(text)
                    },
                    softWrap = false,
                    maxLines = 1,
                    textAlign = TextAlign.Left
                )
            }

            EvenWidthGrid {
                MiscFactText("CR", Monster.prettyChallengeRating(monster.challengeRating))
                MiscFactText("Proficiency", "%+d".format(monster.proficiencyBonus))
                MiscFactText("Size", monster.size.toString())
                MiscFactText("Speed", "${monster.speed}' (${monster.speed / 5} tiles)")
            }

            // Information
            if (monster.information.entries.isNotEmpty()) {
                PaddedDivider()

                InformationDisplay(monster.information)
            }
        }
    }
}
