package makovacs.dnd.ui.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import makovacs.dnd.R
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.CreatureSize
import makovacs.dnd.data.dnd.Description
import makovacs.dnd.data.dnd.Header
import makovacs.dnd.data.dnd.Information
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.Separator
import makovacs.dnd.ui.util.toBitmap

/**
 * [ViewModel] for the list of [monsters][Monster] stored in the encyclopedia.
 */
class MonstersViewModel : ViewModel() {
    private val _monsters = mutableStateListOf<Monster>()

    /**
     * Clears this model's monsters and adds some default monster data.
     *
     * @return This instance.
     */
    @Composable
    fun initializeToDefaultData(): MonstersViewModel {
        _monsters.clear()
        _monsters.add(
            Monster(
                "Gnoll",
                "A scary monster.",
                CreatureSize.MEDIUM,
                15,
                5,
                30,
                AbilityScores(14, 12, 11, 6, 10, 7),
                .5f,
                R.drawable.gnolls.toBitmap(),
                imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.gnolls)),
                listOf("Humanoid", "Chaotic", "Evil"),
                Information(
                    listOf(
                        Description("Rampage", "When the gnoll reduces a creature to 0 hit points with a melee attack on its turn, the gnoll can take a bonus action to move up to half its speed and make a bite attack."),
                        Separator,
                        Header("Actions"),
                        Description("Bite", "Melee Weapon Attack: +4 to hit, reach 5 ft., one creature. Hit: 4 (1d4 + 2) piercing damage."),
                        Description("Spear", "Melee or Ranged Weapon Attack: +4 to hit, reach 5 ft. or range 20/60 ft., one target. Hit: 5 (1d6 + 2) piercing damage, or 6 (1d8 + 2) piercing damage if used with two hands to make a melee attack."),
                        Description("Longbow", "Ranged Weapon Attack: +3 to hit, range 150/600 ft., one target. Hit: 5 (1d8 + 1) piercing damage.")
                    )
                )
            )
        )
        _monsters.add(
            Monster(
                "Wolf",
                "A puppy gone mad.",
                CreatureSize.MEDIUM,
                13,
                2,
                40,
                AbilityScores(12, 15, 12, 3, 12, 6),
                challengeRating = .25f,
                R.drawable.wolf.toBitmap(),
                imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.wolf)),
                listOf("Beast"),
                Information(
                    listOf(
                        Description("Keen Hearing and Smell", "The wolf has advantage on Wisdom (Perception) checks that rely on hearing or smell."),
                        Description("Pack Tactics", "The wolf has advantage on attack rolls against a creature if at least one of the wolf's allies is within 5 feet of the creature and the ally isn't incapacitated."),
                        Separator,
                        Header("Actions"),
                        Description("Bite", "Melee Weapon Attack: +4 to hit, reach 5 ft., one target. Hit: 7 (2d4 + 2) piercing damage. If the target is a creature, it must succeed on a DC 11 Strength saving throw or be knocked prone.")
                    )
                )
            )
        )

        return this
    }

    /**
     * Adds a monster to this model.
     *
     * @param monster The monster to add.
     */
    fun addMonster(monster: Monster) {
        val preexisting = _monsters.find { it == monster }
        if (_monsters.contains(monster)) error("\"$preexisting\" already exists!")

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
