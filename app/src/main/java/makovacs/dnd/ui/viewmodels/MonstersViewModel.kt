// Main coding: Jordan

package makovacs.dnd.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery
import makovacs.dnd.logic.normalizeForInsensitiveComparisons

/**
 * [ViewModel] for the list of [monsters][Monster] stored in the encyclopedia.
 */
class MonstersViewModel : ViewModel() {
    private val repository = MyApp.appModule.monstersRepository

    /**
     * Adds a monster to this model.
     *
     * @param monster The monster to add.
     */
    suspend fun addMonster(monster: Monster) {
        // Check for name conflicts;
        // these aren't strictly enforced, two users might add/edit two monsters with the same
        // resulting name, but we avoid them here:
        val newMonsterComparableName = monster.name.normalizeForInsensitiveComparisons()
        val preexisting = monstersFlow.value?.find { it.name.normalizeForInsensitiveComparisons() == newMonsterComparableName }
        if (preexisting != null) error("\"${preexisting.name}\" already exists!")

        repository.addMonster(monster)
    }

    /**
     * Removes [monster] from this model.
     *
     * @param monster The monster to remove.
     * @return The removed monster.
     */
    fun removeMonster(monster: Monster) {
        viewModelScope.launch { repository.deleteMonster(monster) }
    }

    /**
     * Updates a monster.
     *
     * @param oldMonster The monster before the update.
     * @param newMonster The monster after the update.
     * @return [The updated monster][newMonster].
     */
    fun updateMonster(oldMonster: Monster, newMonster: Monster): Monster {
        // Check for name conflicts;
        // these aren't strictly enforced, two users might add/edit two monsters with the same
        // resulting name, but we avoid them here:
        val newMonsterComparableName = newMonster.name.normalizeForInsensitiveComparisons()
        if (oldMonster.name.normalizeForInsensitiveComparisons() != newMonsterComparableName) {
            // Name has changed, is it conflicting?
            val preexisting = monstersFlow.value?.find { it.name.normalizeForInsensitiveComparisons() == newMonsterComparableName }
            if (preexisting != null) error("\"${preexisting.name}\" already exists!")
        }

        viewModelScope.launch { repository.updateMonster(oldMonster, newMonster) }
        return newMonster
    }

    /**
     * Gets the monster with the given [name][Monster.name].
     *
     * @param id The [Monster.id] of the monster to get.
     * @return The matching monster, or null if there is none or the monsters haven't been loaded
     * yet.
     */
    @Composable
    fun getMonster(id: String): Monster? {
        return monsters?.find { it.id == id }
    }

    /**
     * The value of [monstersFlow] collected as state.
     */
    val monsters @Composable get() = monstersFlow.collectAsState().value

    /**
     * The list of monsters in the model.
     *
     * @return The latest list of monsters, or null if the monsters are still being loaded.
     */
    val monstersFlow by lazy {
        val state = MutableStateFlow<List<Monster>?>(null)
        viewModelScope.launch {
            repository
                .getAtLeast(MonsterQuery("", emptyList(), emptySet()))
                .collect { state.value = it }
        }
        state
    }
}

/**
 * Provides a [MonstersViewModel].
 */
val LocalMonstersViewModel = compositionLocalOf<MonstersViewModel> { error("There is no provided MonstersViewModel") }
