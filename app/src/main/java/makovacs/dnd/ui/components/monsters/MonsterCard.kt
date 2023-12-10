// Main coding: Jordan

package makovacs.dnd.ui.components.monsters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.ui.components.creatures.CreatureImage
import makovacs.dnd.ui.components.creatures.CreatureName
import makovacs.dnd.ui.components.creatures.CreatureTags

/**
 * Displays a summary of a monster's information and stats.
 *
 * @param monster The monster whose stats should be shown.
 */
@Composable
fun MonsterCard(monster: Monster, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(8.dp)) {
        Row {
            CreatureImage(
                monster.imageBitmap,
                monster.imageDesc,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                CreatureName(name = monster.name)
                Divider()
                CreatureTags(tags = monster.tags, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
