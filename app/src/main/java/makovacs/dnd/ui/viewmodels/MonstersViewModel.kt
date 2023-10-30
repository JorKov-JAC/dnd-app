package makovacs.dnd.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import makovacs.dnd.R
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterTag
import makovacs.dnd.ui.util.toBitmap

/**
 * [ViewModel] for the list of [monsters][Monster] stored in the encyclopedia.
 */
class MonstersViewModel: ViewModel() {
    private val _monsters = mutableStateListOf<Monster>()

    /**
     * Clears this model's monsters and adds some default monster data.
     *
     * @return This instance.
     */
    @Composable
    fun initializeToDefaultData(): MonstersViewModel {
        _monsters.clear()
        _monsters.add(Monster(
            "Gnoll",
            "A scary monster.",
            AbilityScores(14, 12, 11, 6, 10, 7),
            listOf(
                MonsterTag("Humanoid", "Stands on two legs."),
                MonsterTag("Medium", "Has a medium size.")
            ),
            R.drawable.gnolls.toBitmap(),
            imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.gnolls))
        ))
        _monsters.add(Monster(
            "Wolf",
            "A puppy gone mad.",
            AbilityScores(12, 15, 12, 3, 12, 6),
            listOf(
                MonsterTag("Beast", "Is a beasty."),
                MonsterTag("Medium", "Has a medium size.")
            ),
            R.drawable.wolf.toBitmap(),
            imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.wolf))
        ))

        return this
    }

    /**
     * Adds a monster to this model.
     *
     * @param monster The monster to add.
     */
    fun addMonster(monster: Monster) {
        if (_monsters.contains(monster)) error("Monster already exists!")
        _monsters.add(monster)
    }

    /**
     * Removes a [Monster] based on its [name][Monster.name].
     *
     * @param name The [name][Monster.name] of the monster to remove.
     * @return The removed monster.
     * @throws IllegalStateException Thrown if there is no monster with [name] in the model.
     */
    fun removeMonster(name: String): Monster {
        val index = _monsters.indexOfFirst { it.name == name }
        if (index < 0) error("Monster with name \"$name\" not found")
        return _monsters.removeAt(index)
    }

    /**
     * Updates a monster.
     *
     * @param oldName The [Monster's name][Monster.name] before the update.
     * @param updatedMonster The monster after the update.
     * @return [The updated monster][updatedMonster].
     */
    fun updateMonster(oldName: String, updatedMonster: Monster): Monster {
        removeMonster(oldName)
        addMonster(updatedMonster)
        return updatedMonster
    }

    /**
     * Gets the monster with the given [name][Monster.name].
     *
     * @param name The [Monster.name] of the monster to get.
     * @return The matching monster, or null if there is none.
     */
    fun getMonster(name: String): Monster? = monsters.find { it.name == name }

    /**
     * The list of monsters in the model.
     */
    val monsters get() = _monsters.toList()
}

// TODO Temporary(?) until Monster data is persisted and we can safely recreate the VM:
val LocalMonstersViewModel = compositionLocalOf<MonstersViewModel> { error("There is no provided MonstersViewModel") }
