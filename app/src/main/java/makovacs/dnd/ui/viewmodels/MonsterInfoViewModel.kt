package makovacs.dnd.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import makovacs.dnd.data.dnd.AbilityScores
import makovacs.dnd.data.dnd.CreatureSize
import makovacs.dnd.data.dnd.InformationEntry
import makovacs.dnd.data.dnd.Monster

/**
 * View-model which stores information from which a [Monster] can be created.
 *
 * @param name Name for [Monster.name]
 * @param description Description for [Monster.description]
 * @param size Size for [Monster.size]
 * @param armorClass Number for [Monster.armorClass]
 * @param hitDiceCount Number for [Monster.hitDiceCount]
 * @param speed Number for [Monster.speed]
 * @param abilityScores Every ability score for [Monster.abilityScores] in the order of
 * [AbilityScores]'s constructor.
 * @param challengeRating Number for [Monster.challengeRating]
 * @param imageBitmap Image bitmap for [Monster.imageBitmap]
 * @param imageDesc Image description for [Monster.imageDesc]
 * @param tags Tags for [Monster.tags].
 * @param informationEntries Information entries for [Monster.information].
 */
class MonsterInfoViewModel(
    name: String,
    description: String,
    size: CreatureSize?,
    armorClass: Int?,
    hitDiceCount: Int?,
    speed: Int?,
    abilityScores: List<Int?>,
    challengeRating: Float?,
    imageBitmap: Bitmap?,
    imageDesc: String?,
    tags: List<String>,
    informationEntries: List<InformationEntry>
) : ViewModel() {
    var name by mutableStateOf(name)
    var description by mutableStateOf(description)
    var size by mutableStateOf(size)
    var armorClass by mutableStateOf(armorClass)
    var hitDiceCount by mutableStateOf(hitDiceCount)
    var speed by mutableStateOf(speed)
    var abilityScores = abilityScores.toMutableStateList()
    var challengeRating by mutableStateOf(challengeRating)
    var imageBitmap by mutableStateOf(imageBitmap)
    var imageDesc by mutableStateOf(imageDesc)
    val tags = tags.toMutableStateList()
    val informationEntries = informationEntries.toMutableStateList()

    var errorMsg by mutableStateOf<String?>(null)

    /**
     * Creates a new instance with empty data.
     */
    constructor() : this(
        "", "", null, null, null, null,
        abilityScores = listOf(null, null, null, null, null, null), null,
        null, null, emptyList(), emptyList()
    )

    /**
     * Creates a new instance based on an existing monster.
     *
     * @param monster The monster to base this instance's data off of.
     */
    constructor(monster: Monster) : this(
        name = monster.name, description = monster.rawDescription, size = monster.size,
        armorClass = monster.armorClass, hitDiceCount = monster.hitDiceCount, speed = monster.speed,
        abilityScores = monster.abilityScores.scoreList, challengeRating = monster.challengeRating,
        imageBitmap = monster.imageBitmap, imageDesc = monster.imageDesc, tags = monster.tags,
        informationEntries = monster.information.entries
    )

    /**
     * Tries to create an [AbilityScores] from [abilityScores].
     *
     * @throws [IllegalStateException] Thrown when an [AbilityScores] cannot be created.
     */
    fun getAbilityScores(): AbilityScores {
        // Ensure all scores are set.
        for ((i, score) in abilityScores.withIndex()) {
            score ?: error("${AbilityScores.abilityNames[i]} must be set.")
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return AbilityScores.from(abilityScores as Iterable<Int>)
        } catch (ex: IllegalArgumentException) {
            // Rethrow as an IllegalStateException:
            @Suppress("UNREACHABLE_CODE") // No idea why it believes this is unreachable
            throw error(ex)
        }
    }
}
