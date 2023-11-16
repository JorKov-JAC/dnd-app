package makovacs.dnd.ui.screens.monsters

import android.icu.text.DecimalFormat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import makovacs.dnd.R
import makovacs.dnd.data.dnd.CreatureSize
import makovacs.dnd.data.dnd.Information
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.logic.normalizeAndClean
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import makovacs.dnd.logic.swap
import makovacs.dnd.ui.components.AbilityScoresInput
import makovacs.dnd.ui.components.EditableStringList
import makovacs.dnd.ui.components.MonsterBitmapSelector
import makovacs.dnd.ui.components.NullableIntField
import makovacs.dnd.ui.components.StringDropdownSelector
import makovacs.dnd.ui.components.common.InformationEditor
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.MonsterInfoViewModel

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
            navHostController.popBackStack()
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
fun NewMonsterEditor(
    modifier: Modifier = Modifier,
    vm: MonsterInfoViewModel = viewModel(),
    onSubmit: (newMonster: Monster) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(4.dp)
    ) {
        // Monster image
        MonsterBitmapSelector(
            vm.imageBitmap,
            { bitmap, desc ->
                vm.imageBitmap = bitmap
                vm.imageDesc = desc
            },
            modifier = Modifier
                .sizeIn(10.dp, 10.dp, 150.dp, 150.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Name
        TextField(
            vm.name,
            { vm.name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Description
        TextField(
            vm.description,
            { vm.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Size
        StringDropdownSelector(
            choices = CreatureSize.values().toList(),
            value = vm.size,
            setValue = { _, it -> vm.size = it },
            reset = { vm.size = null },
            label = "Size"
        )

        // Armor Class
        NullableIntField(
            vm.armorClass,
            { vm.armorClass = it },
            label = "Armor Class",
            modifier = Modifier.fillMaxWidth()
        )

        // Hit Dice Count
        NullableIntField(
            vm.hitDiceCount,
            { vm.hitDiceCount = it },
            label = "Number of Hit Dice",
            modifier = Modifier.fillMaxWidth()
        )

        // Speed
        NullableIntField(
            vm.speed,
            { vm.speed = it },
            label = "Speed (multiple of 5)",
            modifier = Modifier.fillMaxWidth()
        )

        // Ability scores
        AbilityScoresInput(
            vm.abilityScores,
            vm.abilityScores::set,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        )

        // Challenge Rating
        StringDropdownSelector(
            choices = Monster.validChallengeRatings,
            value = vm.challengeRating,
            setValue = { _, it -> vm.challengeRating = it },
            reset = { vm.challengeRating = null },
            label = "Challenge Rating",
            choiceName = { DecimalFormat("0.###").format(it) }
        )

        // Tags
        EditableStringList(
            mutableList = vm.tags,
            singleLine = true,
            title = stringResource(R.string.tags),
            cleaner = String::normalizeAndClean,
            validator = {
                // Make sure it isn't blank
                if (it.isBlank()) return@EditableStringList "Must not be blank!"

                return@EditableStringList null // No error
            },
            uniqueOn = String::normalizeForInsensitiveComparisons
        )

        // Information
        InformationEditor(
            entries = vm.informationEntries,
            swap = vm.informationEntries::swap,
            insert = vm.informationEntries::add,
            delete = vm.informationEntries::removeAt
        )

        // Show errors messages
        if (vm.errorMsg !== null) {
            Text(vm.errorMsg!!, color = MaterialTheme.colorScheme.error)
        }

        // Submit button
        Button(onClick = {
            vm.errorMsg = try {
                onSubmit(
                    Monster(
                        vm.name.normalizeAndClean(),
                        vm.description,
                        vm.size ?: error("Missing Size."),
                        vm.armorClass ?: error("Missing Armor Class."),
                        vm.hitDiceCount ?: error("Missing Hit Dice count."),
                        vm.speed ?: error("Missing Speed."),
                        vm.getAbilityScores(),
                        vm.challengeRating ?: error("Missing Challenge Rating."),
                        vm.imageBitmap,
                        vm.imageDesc,
                        vm.tags,
                        Information(vm.informationEntries)
                    )
                )
                null // No error
            } catch (e: Exception) {
                // TODO For some exceptions (IllegalArgumentException), this displays the
                //      exception's type.
                e.localizedMessage
            }
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.create))
        }
    }
}
