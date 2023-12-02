package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.ConfirmDeleteDialog
import makovacs.dnd.ui.components.MonsterDetails
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

/**
 * Shows detailed information about a given monster.
 *
 * @param monster The monster to show information for.
 *
 * If null, will navigate upward; this is useful when the user goes to a monster's page,
 * then goes to the monster list, then deletes the monster, then navigates backward to the
 * individual monster's page again. The monster will no longer exist, so they will navigate further
 * back.
 * @param onDelete Called when the user tries to delete a Monster. Null if user cannot delete the
 * monster.
 */
@Composable
fun MonsterDetailsScreen(
    monster: Monster?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val navHostController = LocalNavHostController.current

    if (monster == null) {
        // Probably navigated backward to a deleted/moved entry, go further back

        // We're fudging the "no side-effects" rule here by navigating like this, so make sure we
        // only do it once:
        var alreadyNavigatedUp by rememberSaveable { mutableStateOf(false) }
        if (!alreadyNavigatedUp) navHostController.navigateUp()
        alreadyNavigatedUp = true

        return
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        var deleteConfirmationVisible by rememberSaveable { mutableStateOf(false) }

        Box {
            // Overlay buttons on top of the monster information
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
                    .padding(16.dp)
                    .width(IntrinsicSize.Min)
            ) {
                // Edit Button
                OutlinedButton(
                    { navHostController.navigate(Route.EditMonsterRoute.go(monster.name)) },
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, null /* Described by Text */)
                    Spacer(
                        Modifier
                            .weight(1f)
                            .width(4.dp))
                    Text("Edit")
                }

                // Delete Button
                if (onDelete != null) {
                    Button(
                        { deleteConfirmationVisible = true },
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, null /* Described by Text */)
                        Spacer(
                            Modifier
                                .weight(1f)
                                .width(4.dp))
                        Text("Delete")
                    }
                }
            }

            // Monster information
            MonsterDetails(monster = monster, modifier = Modifier.padding(8.dp))
        }

        // Delete confirmation dialog
        if (deleteConfirmationVisible) {
            ConfirmDeleteDialog(
                onCloseDialog = { deleteConfirmationVisible = false },
                itemStr = monster.name,
                onDelete = onDelete!!
            )
        }
    }
}
