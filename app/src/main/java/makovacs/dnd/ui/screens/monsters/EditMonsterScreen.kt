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
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.monsters.MonsterEditor
import makovacs.dnd.ui.components.monsters.MonsterEditorViewModel

/**
 * Main coding: Jordan
 * Allows the user to edit a monster's existing information.
 *
 * @param monster The monster to edit.
 * @param onSubmit Called when the user submits their edits and is passed the original [monster] and
 * the new monster.
 * Should throw with a user-readable error message on failure.
 */
@Composable
fun EditMonsterScreen(
    monster: Monster,
    modifier: Modifier = Modifier,
    onSubmit: (oldMonster: Monster, newMonster: Monster) -> Unit
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text(
            "Editing \"${monster.name}\"",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        MonsterEditor(
            vm = viewModel { MonsterEditorViewModel(monster) },
            submitButtonText = stringResource(R.string.save),
            submittedMonsterId = monster.id,
            submittedMonsterOwnerUserId = monster.ownerUserId
        ) {
            onSubmit(monster, it)
        }
    }
}
