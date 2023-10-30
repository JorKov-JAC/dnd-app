package makovacs.dnd.data.dnd

import android.graphics.Bitmap
import kotlin.IllegalArgumentException

/**
 * Contains information for a D&D monster.
 *
 * Two instances are considered equal if their [names][name] match.
 *
 * @param name The monster's name.
 * @param description A full description of the monster.
 * @param abilityScores The monster's ability scores.
 * @param tags A list of tags categorizing the monster, ex. "Humanoid", "Medium".
 * @param imageBitmap Bitmap image of the monster.
 * @param imageDesc Optional description of [imageBitmap], or null if [imageBitmap] is null.
 */
data class Monster(
    val name: String,
    private val description: String,
    val abilityScores: AbilityScores,
    val tags: List<MonsterTag>,
    val imageBitmap: Bitmap?,
    val imageDesc: String?) {

    val descriptionOrDefault get() = description.ifBlank { "No description." }

    init {
        if (name.isBlank()) throw IllegalArgumentException("Name cannot be blank!")
        if (name.trim() != name) throw IllegalArgumentException("Name cannot have surrounding whitespace!")
        if (imageBitmap == null && imageDesc != null) throw IllegalArgumentException("There is no image, so there should be no image description!")
    }

    /** Unique Monster ID */
    val id get() = name

    override fun equals(other: Any?) = other is Monster && name == other.name
    override fun hashCode() = name.hashCode()
    override fun toString() = name
}