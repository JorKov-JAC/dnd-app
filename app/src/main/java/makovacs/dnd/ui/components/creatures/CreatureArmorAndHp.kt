// Main coding: Jordan

package makovacs.dnd.ui.components.creatures

import android.icu.text.DecimalFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.common.Dice

/**
 * Display's information about a creature's Armor Class and Hit Points.
 *
 * @param ac The creature's Armor Class.
 * @param hitDice The creature's Hit Dice.
 * @param averageHp The creature's Average Hit Points.
 */
@Composable
fun CreatureArmorAndHp(ac: Int, hitDice: Dice, averageHp: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .height(with(LocalDensity.current) { 96.sp.toDp() })
            .padding(vertical = 8.dp)
            .then(modifier)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                ac.toString(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Image(
                painterResource(R.drawable.ac_shield),
                "Armor Class"
            )
        }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Image(
                painterResource(R.drawable.hp_heart),
                "Hit Points"
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                "${hitDice}\n(${DecimalFormat("0.##").format(averageHp)})",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}
