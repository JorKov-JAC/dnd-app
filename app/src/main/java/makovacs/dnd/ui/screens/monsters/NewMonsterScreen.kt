// Main coding: Jordan

package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.R
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.ui.components.monsters.MonsterEditor

/**
 * Allows the user to input information to create a new [Monster].
 *
 * @param userId The id to use for [Monster.ownerUserId].
 * @param onSubmit Called with the new [Monster].
 * Should throw with a user-readable error message on failure.
 */
@Composable
fun NewMonsterScreen(
    userId: String?,
    modifier: Modifier = Modifier,
    onSubmit: (newMonster: Monster) -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text(
            stringResource(id = R.string.newMonster),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        MonsterEditor(
            submitButtonText = stringResource(R.string.create),
            vm = viewModel(),
            submittedMonsterId = null,
            submittedMonsterOwnerUserId = userId,
            onSubmit = onSubmit
        )
    }
}
