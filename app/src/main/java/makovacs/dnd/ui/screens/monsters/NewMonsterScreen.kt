package makovacs.dnd.ui.screens.monsters

import android.graphics.Bitmap
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.ui.components.AbilityScoresInput
import makovacs.dnd.ui.components.MonsterBitmapSelector
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

/**
 * Allows the user to input information to create a new [Monster].
 *
 * @param onSubmit Called with the new [Monster].
 * Should throw with a user-readable error message on failure.
 */
@Composable
fun NewMonsterScreen(modifier: Modifier = Modifier, onSubmit: (newMonster: Monster) -> Unit) {
    val navHostController = LocalNavHostController.current

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Text(
            stringResource(id = R.string.newMonster),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        NewMonsterEditor {
            onSubmit(it)
            navHostController.navigate(Route.MonsterDetailsRoute.go(it.name))
        }
    }
}

/**
 * Allows the user to input information to create a new [Monster].
 *
 * @param onSubmit Called with the new [Monster].
 * Should throw with a user-readable error message on failure.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMonsterEditor(modifier: Modifier = Modifier, onSubmit: (newMonster: Monster) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var abilityScores by rememberSaveable { mutableStateOf(AbilityScores(0, 0, 0, 0, 0, 0)) }
    var imageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var imageDesc by rememberSaveable { mutableStateOf<String?>(null) }

    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(4.dp)
    ) {
        // Monster image
        MonsterBitmapSelector(
            imageBitmap,
            { bitmap, desc ->
                imageBitmap = bitmap
                imageDesc = desc
            },
            modifier = Modifier
                .sizeIn(10.dp, 10.dp, 150.dp, 150.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Name
        TextField(
            name,
            { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Description
        TextField(
            description,
            { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Ability scores
        AbilityScoresInput(
            abilityScores,
            { abilityScores = it },
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        )

        // Show errors messages
        if (errorMsg !== null) {
            Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
        }

        // Submit button
        Button(onClick = {
            errorMsg = try {
                onSubmit(Monster(name, description, abilityScores, emptyList(), imageBitmap, imageDesc))
                null // No error
            } catch (e: Exception) {
                e.localizedMessage
            }
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.create))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewMonsterPreview() {
    NewMonsterEditor {}
}
