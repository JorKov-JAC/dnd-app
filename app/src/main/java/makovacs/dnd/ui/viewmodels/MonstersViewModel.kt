package makovacs.dnd.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.CreatureSize
import makovacs.dnd.data.dnd.Description
import makovacs.dnd.data.dnd.Header
import makovacs.dnd.data.dnd.Information
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery
import makovacs.dnd.data.dnd.Separator

///**
// * [ViewModel] for the list of [monsters][Monster] stored in the encyclopedia.
// */
//class MonstersViewModel : ViewModel() {
//    private val _monsters = mutableStateListOf<Monster>()
//
//    /**
//     * Clears this model's monsters and adds some default monster data.
//     *
//     * @return This instance.
//     */
//    @Composable
//    fun initializeToDefaultData(): MonstersViewModel {
//        _monsters.clear()
//        _monsters.add(
//            Monster(
//                "Gnoll",
//                "A scary monster.",
//                CreatureSize.MEDIUM,
//                15,
//                5,
//                30,
//                AbilityScores(14, 12, 11, 6, 10, 7),
//                .5f,
//                R.drawable.gnolls.toBitmap(),
//                imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.gnolls)),
//                listOf("Humanoid", "Chaotic", "Evil"),
//                Information(
//                    listOf(
//                        Description("Rampage", "When the gnoll reduces a creature to 0 hit points with a melee attack on its turn, the gnoll can take a bonus action to move up to half its speed and make a bite attack."),
//                        Separator(),
//                        Header("Actions"),
//                        Description("Bite", "Melee Weapon Attack: +4 to hit, reach 5 ft., one creature. Hit: 4 (1d4 + 2) piercing damage."),
//                        Description("Spear", "Melee or Ranged Weapon Attack: +4 to hit, reach 5 ft. or range 20/60 ft., one target. Hit: 5 (1d6 + 2) piercing damage, or 6 (1d8 + 2) piercing damage if used with two hands to make a melee attack."),
//                        Description("Longbow", "Ranged Weapon Attack: +3 to hit, range 150/600 ft., one target. Hit: 5 (1d8 + 1) piercing damage.")
//                    )
//                )
//            )
//        )
//        _monsters.add(
//            Monster(
//                "Wolf",
//                "A puppy gone mad.",
//                CreatureSize.MEDIUM,
//                13,
//                2,
//                40,
//                AbilityScores(12, 15, 12, 3, 12, 6),
//                challengeRating = .25f,
//                R.drawable.wolf.toBitmap(),
//                imageDesc = stringResource(R.string.imageOf_format, stringResource(R.string.wolf)),
//                listOf("Beast"),
//                Information(
//                    listOf(
//                        Description("Keen Hearing and Smell", "The wolf has advantage on Wisdom (Perception) checks that rely on hearing or smell."),
//                        Description("Pack Tactics", "The wolf has advantage on attack rolls against a creature if at least one of the wolf's allies is within 5 feet of the creature and the ally isn't incapacitated."),
//                        Separator(),
//                        Header("Actions"),
//                        Description("Bite", "Melee Weapon Attack: +4 to hit, reach 5 ft., one target. Hit: 7 (2d4 + 2) piercing damage. If the target is a creature, it must succeed on a DC 11 Strength saving throw or be knocked prone.")
//                    )
//                )
//            )
//        )
//
//        return this
//    }
//
//    /**
//     * Adds a monster to this model.
//     *
//     * @param monster The monster to add.
//     */
//    fun addMonster(monster: Monster) {
//        val comparisonName = monster.name.normalizeForInsensitiveComparisons()
//        val preexisting = _monsters.find { it.name.normalizeForInsensitiveComparisons() == comparisonName }
//        if (preexisting != null) error("\"${preexisting.name}\" already exists!")
//
//        _monsters.add(monster)
//    }
//
//    /**
//     * Removes a [Monster] based on its [name][Monster.name].
//     *
//     * @param name The [name][Monster.name] of the monster to remove.
//     * @return The removed monster.
//     * @throws IllegalStateException Thrown if there is no monster with [name] in the model.
//     */
//    fun removeMonster(name: String): Monster {
//        val index = _monsters.indexOfFirst { it.name == name }
//        if (index < 0) error("Monster with name \"$name\" not found")
//        return _monsters.removeAt(index)
//    }
//
//    /**
//     * Updates a monster.
//     *
//     * @param oldName The [Monster's name][Monster.name] before the update.
//     * @param updatedMonster The monster after the update.
//     * @return [The updated monster][updatedMonster].
//     */
//    fun updateMonster(oldName: String, updatedMonster: Monster): Monster {
//        removeMonster(oldName)
//        addMonster(updatedMonster)
//        return updatedMonster
//    }
//
//    /**
//     * Gets the monster with the given [name][Monster.name].
//     *
//     * @param name The [Monster.name] of the monster to get.
//     * @return The matching monster, or null if there is none.
//     */
//    fun getMonster(name: String): Monster? = monsters.find { it.name == name }
//
//    /**
//     * The list of monsters in the model.
//     */
//    val monsters get() = _monsters.toList()
//}

/**
 * [ViewModel] for the list of [monsters][Monster] stored in the encyclopedia.
 */
class MonstersViewModel : ViewModel() {
    val repository = MyApp.appModule.monstersRepository

    /**
     * Clears this model's monsters and adds some default monster data.
     *
     * @return This instance.
     */
    suspend fun initializeToDefaultData(gnollsBitmap: Bitmap, wolfBitmap: Bitmap): MonstersViewModel {
        if (repository.getMonster("Gnoll") == null) {
            repository.addMonster(
                Monster(
                    "Gnoll",
                    "A scary monster.",
                    CreatureSize.MEDIUM,
                    15,
                    5,
                    30,
                    AbilityScores(14, 12, 11, 6, 10, 7),
                    .5f,
                    gnollsBitmap,
                    "Image of Gnolls",
                    listOf("Humanoid", "Chaotic", "Evil"),
                    Information(
                        listOf(
                            Description(
                                "Rampage",
                                "When the gnoll reduces a creature to 0 hit points with a melee attack on its turn, the gnoll can take a bonus action to move up to half its speed and make a bite attack."
                            ),
                            Separator(),
                            Header("Actions"),
                            Description(
                                "Bite",
                                "Melee Weapon Attack: +4 to hit, reach 5 ft., one creature. Hit: 4 (1d4 + 2) piercing damage."
                            ),
                            Description(
                                "Spear",
                                "Melee or Ranged Weapon Attack: +4 to hit, reach 5 ft. or range 20/60 ft., one target. Hit: 5 (1d6 + 2) piercing damage, or 6 (1d8 + 2) piercing damage if used with two hands to make a melee attack."
                            ),
                            Description(
                                "Longbow",
                                "Ranged Weapon Attack: +3 to hit, range 150/600 ft., one target. Hit: 5 (1d8 + 1) piercing damage."
                            )
                        )
                    )
                )
            )
        }
        if (repository.getMonster("Wolf") == null) {
            repository.addMonster(
                Monster(
                    "Wolf",
                    "A puppy gone mad.",
                    CreatureSize.MEDIUM,
                    13,
                    2,
                    40,
                    AbilityScores(12, 15, 12, 3, 12, 6),
                    challengeRating = .25f,
                    wolfBitmap,
                    imageDesc = "Image of a Wolf",
                    listOf("Beast"),
                    Information(
                        listOf(
                            Description(
                                "Keen Hearing and Smell",
                                "The wolf has advantage on Wisdom (Perception) checks that rely on hearing or smell."
                            ),
                            Description(
                                "Pack Tactics",
                                "The wolf has advantage on attack rolls against a creature if at least one of the wolf's allies is within 5 feet of the creature and the ally isn't incapacitated."
                            ),
                            Separator(),
                            Header("Actions"),
                            Description(
                                "Bite",
                                "Melee Weapon Attack: +4 to hit, reach 5 ft., one target. Hit: 7 (2d4 + 2) piercing damage. If the target is a creature, it must succeed on a DC 11 Strength saving throw or be knocked prone."
                            )
                        )
                    )
                )
            )
        }

        return this
    }

    /**
     * Adds a monster to this model.
     *
     * @param monster The monster to add.
     */
    suspend fun addMonster(monster: Monster) {
        val preexisting = repository
            .getMonster(monster.name)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
            .value
        if (preexisting != null) error("\"${preexisting.name}\" already exists!")

        repository.addMonster(monster)
    }

    /**
     * Removes [monster] from the compendium.
     *
     * @param monster The monster to remove.
     * @return The removed monster.
     */
    fun removeMonster(monster: Monster) {
        runBlocking { repository.deleteMonster(monster) }
//        return getMonster("Gnoll")!! // TODO
//        val index = _monsters.indexOfFirst { it.name == name }
//        if (index < 0) error("Monster with name \"$name\" not found")
//        return _monsters.removeAt(index)
    }

    /**
     * Updates a monster.
     *
     * @param oldMonster The monster before the update.
     * @param newMonster The monster after the update.
     * @return [The updated monster][newMonster].
     */
    fun updateMonster(oldMonster: Monster, newMonster: Monster): Monster {
        runBlocking { repository.updateMonster(oldMonster, newMonster) }
        return newMonster
//        removeMonster(oldName)
//        addMonster(updatedMonster)
//        return updatedMonster
    }

    /**
     * Gets the monster with the given [name][Monster.name].
     *
     * @param name The [Monster.name] of the monster to get.
     * @return The matching monster, or null if there is none or the monsters haven't been loaded
     * yet.
     */
    @Composable
    fun getMonster(name: String): Monster? {
        val monsters = monsters.collectAsState().value
        println(monsters)
        return monsters?.find { it.name == name }
    }

//    private var lastFetchTime: Date = Date(0)
//    private var cachedMonsters: List<Monster>? = null
//    /**
//     * The list of monsters in the model.
//     */
//    val monsters: List<Monster> get() = if (Date().time - lastFetchTime.time > 5000) {
//        cachedMonsters = runBlocking { repository.queryMonsters(MonsterQuery("", emptyList(), emptySet())) }
//        lastFetchTime = Date()
//        cachedMonsters!!
//    } else {
//        println("Used cache!")
//        cachedMonsters!!
//    }
    /**
     * The list of monsters in the model.
     *
     * @return The latest list of monsters, or null if the monsters are still being loaded.
     */
    val monsters by lazy {
        val state = MutableStateFlow<List<Monster>?>(null)
        viewModelScope.launch {
            repository
                .queryMonsters(MonsterQuery("", emptyList(), emptySet()))
                .collect {
                    state.value = it
                    println("~~~~~~~~")
                    println(it)
                    println(state.value)
                    println("~~~~~~~~")
                }
        }
        state
    }
//    val monsters: StateFlow<List<Monster>?> by lazy {
//        repository
//            .queryMonsters(MonsterQuery("", emptyList(), emptySet()))
//            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
//    }
//    @get:Composable
//    val monsters: List<Monster>? by lazy {
//        var state by mutableStateOf(null)
//        @SuppressLint("FlowOperatorInvokedInComposition") @Composable get() = repository
//            .queryMonsters(MonsterQuery("", emptyList(), emptySet()))
//            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
//            .collectAsState()
//            .value
//    }
}

// TODO Temporary(?) until Monster data is persisted and we can safely recreate the VM:
val LocalMonstersViewModel = compositionLocalOf<MonstersViewModel> { error("There is no provided MonstersViewModel") }
