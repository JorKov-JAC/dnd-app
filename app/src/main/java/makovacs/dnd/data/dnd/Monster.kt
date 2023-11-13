package makovacs.dnd.data.dnd

import android.graphics.Bitmap
import makovacs.dnd.logic.normalizeForInsensitiveComparisons

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
    val imageDesc: String?
) {

    val descriptionOrDefault get() = description.ifBlank { "No description." }

    init {
        if (name.isBlank()) throw IllegalArgumentException("Name cannot be blank!")
        if (name.trim() != name) throw IllegalArgumentException("Name cannot have surrounding whitespace!")
        if (imageBitmap == null && imageDesc != null) throw IllegalArgumentException("There is no image, so there should be no image description!")
    }

    /** Unique Monster ID */
    val id get() = name

    /**
     * Checks if [other] is equal to this.
     *
     * Note that equality is based on whether the two objects would conflict
     * (i.e. their [ids][id] would match), not necessarily that all values are equal.
     */
    override fun equals(other: Any?) = other is Monster &&
        id.normalizeForInsensitiveComparisons() == other.id.normalizeForInsensitiveComparisons()
    override fun hashCode() = name.hashCode()
    override fun toString() = name
}

/**
 * Represents a search query for [monsters][Monster].
 *
 * @param name A name to search for.
 * @param positiveTags Tags which must be in the monsters.
 * @param negativeTags Tags which must not be in the monsters.
 */
data class MonsterQuery(
    val name: String,
    val positiveTags: List<String>,
    val negativeTags: Set<String>
) {
    companion object {
        /**
         * Parses a query from [str].
         *
         * @param str The string to parse.
         *
         * Example: If [str] were "gn +Humanoid -Beast -Has a face",
         * then [name] would be "gn", [positiveTags] would be { "Humanoid" }, and [negativeTags]
         * would be { "Beast", "Has a face" }.
         */
        fun fromString(str: String): MonsterQuery {
            val matches = Regex("""[+-]?[^+-]+""").findAll(str)

            var name = ""
            val positiveTags = mutableListOf<String>()
            val negativeTags = mutableSetOf<String>()

            matches
                .map { it.value.normalizeForInsensitiveComparisons() }
                .forEach {
                    // Based on first character, it's either a name or positive/negative tag
                    val firstChar = it[0]
                    when (firstChar) {
                        '+', '-' -> {
                            @Suppress("NAME_SHADOWING")
                            val it = it.substring(1).normalizeForInsensitiveComparisons()

                            if (it.isNotEmpty()) {
                                if (firstChar == '+') {
                                    positiveTags.add(it)
                                } else {
                                    negativeTags.add(it)
                                }
                            }
                        }
                        else -> name = it // Only possible for first match
                    }
                }

            return MonsterQuery(name, positiveTags = positiveTags, negativeTags = negativeTags)
        }
    }

    /**
     * Checks if [monster] fits this query.
     *
     * @param monster The monster to test.
     * @return True if [monster] matches this query, otherwise false.
     */
    fun matches(monster: Monster) = monster.name.normalizeForInsensitiveComparisons().contains(name) &&
        positiveTags.all { positiveTag -> monster.tags.any { it.name.normalizeForInsensitiveComparisons() == positiveTag } } &&
        monster.tags.all { it.name.normalizeForInsensitiveComparisons() !in negativeTags }
}
