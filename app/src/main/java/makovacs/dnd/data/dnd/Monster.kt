package makovacs.dnd.data.dnd

import android.graphics.Bitmap
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import kotlin.math.floor
import kotlin.math.truncate

/**
 * Contains information for a D&D monster.
 *
 * Two instances are considered equal if their [names][name] match.
 *
 * @param
 * @param name The monster's name.
 * @param description A full description of the monster.
 * @param abilityScores The monster's [Ability Scores](https://www.dndbeyond.com/sources/basic-rules/monsters#AbilityScores).
 * @param armorClass The monster's [Armor Class](https://www.dndbeyond.com/sources/basic-rules/monsters#ArmorClass).
 * @param size The monster's [size](https://www.dndbeyond.com/sources/basic-rules/monsters#Size).
 * @param hitDiceCount The number of Hit Dice to use when determining the monster's
 * [Hit Points](https://www.dndbeyond.com/sources/basic-rules/monsters#HitPoints).
 * @param speed The monster's [walking speed](https://www.dndbeyond.com/sources/basic-rules/monsters#Speed).
 * @param challengeRating The monster's [Challenge Rating](https://www.dndbeyond.com/sources/basic-rules/monsters#Challenge).
 * @param imageBitmap Bitmap image of the monster.
 * @param imageDesc Optional description of [imageBitmap], or null if [imageBitmap] is null.
 * @param tags A list of tags categorizing the monster, ex. "Humanoid", "Evil".
 * @param information Facts describing the monster.
 */
data class Monster(
    val name: String,
    private val description: String,
    val size: CreatureSize,
    val armorClass: Int,
    val hitDiceCount: Int,
    val speed: Int,
    val abilityScores: AbilityScores,
    val challengeRating: Float,
    val imageBitmap: Bitmap?,
    val imageDesc: String?,
    val tags: List<String>,
    val information: Information,
) {
    init {
        if (name.isBlank()) throw IllegalArgumentException("Name cannot be blank.")
        if (name.trim() != name) throw IllegalArgumentException("Name cannot have surrounding whitespace.")
        // No longer makes sense since bitmaps might not be loaded immediately:
        // if (imageBitmap == null && imageDesc != null) throw IllegalArgumentException("There is no image, so there should be no image description.")
        if (armorClass < 0) throw IllegalArgumentException("Armor class must be non-negative.")
        if (hitDiceCount <= 0) throw IllegalArgumentException("Must have at least 1 hit die.")
        if (speed < 0 || // Non-negative
            speed % 5 != 0 // Multiple of 5
        ) {
            throw IllegalArgumentException("Speed must be a non-negative multiple of 5.")
        }
        if (challengeRating !in 0f..30f || // Outside allowed range
            floor(challengeRating) != challengeRating && // Not an integer
            challengeRating !in listOf(.5f, .25f, .125f) // Not one of the allowed fractions
        ) {
            throw IllegalArgumentException("Ability score must be an integer within 0..30 or 1/2, 1/4, or 1/8.")
        }
    }

    companion object {
        /**
         * Possible values for [challengeRating].
         */
        val validChallengeRatings = listOf(0f, .125f, .25f, .5f) + (1..30).map { it.toFloat() }

        /**
         * Converts a [Challenge Rating][Monster.challengeRating] number to a pretty string.
         *
         * @param cr The Challenge Rating to convert.
         */
        fun prettyChallengeRating(cr: Float) = when (cr) {
            .125f -> "1/8"
            .25f -> "1/4"
            .5f -> "1/2"
            else -> cr.toInt().toString()
        }
    }

    /** [description] without modifications. */
    val rawDescription get() = description

    /** [description], or a default description if it is blank. */
    val descriptionOrDefault get() = description.ifBlank { "No description." }

    /**
     * The Proficiency Bonus based on [challengeRating].
     *
     * See [this table](https://www.dndbeyond.com/sources/basic-rules/monsters#ProficiencyBonusbyChallengeRating).
     */
    val proficiencyBonus get() = (truncate((challengeRating - 1) / 4) + 2).toInt()

    /**
     * The Experience Point reward based on [challengeRating].
     *
     * See [this table](https://www.dndbeyond.com/sources/basic-rules/monsters#ProficiencyBonusbyChallengeRating).
     */
    val experiencePoints
        get() = when (challengeRating) {
            in 0f..1f -> challengeRating * 200f
            2f -> 450
            3f -> 700
            4f -> 1_100
            5f -> 1_800
            6f -> 2_300
            7f -> 2_900
            8f -> 3_900
            9f -> 5_000
            10f -> 5_900
            11f -> 7_200
            12f -> 8_400
            13f -> 10_000
            14f -> 11_500
            15f -> 13_000
            16f -> 15_000
            17f -> 18_000
            18f -> 20_000
            19f -> 22_000
            20f -> 25_000
            21f -> 33_000
            22f -> 41_000
            23f -> 50_000
            24f -> 62_000
            25f -> 75_000
            26f -> 90_000
            27f -> 105_000
            28f -> 120_000
            29f -> 135_000
            30f -> 155_000
            else -> error("Invalid Challenge Rating")
        }

    /**
     * The hit dice based on [size] and [hitDiceCount].
     *
     * See [this table](https://www.dndbeyond.com/sources/basic-rules/monsters#HitDicebySize).
     */
    val hitDice: Dice
        get() {
            val sides = when (size) {
                CreatureSize.TINY -> 4
                CreatureSize.SMALL -> 6
                CreatureSize.MEDIUM -> 8
                CreatureSize.LARGE -> 10
                CreatureSize.HUGE -> 12
                CreatureSize.GARGANTUAN -> 20
            }

            return Dice(hitDiceCount, sides)
        }

    /**
     * The monster's average hit points based on its [hitDice] and [constitution][abilityScores].
     *
     * See [this](https://www.dndbeyond.com/sources/basic-rules/monsters#HitPoints).
     */
    // TODO Handle case where constitution modifier is negative (min hit-points is 1, so then the
    //      average should consider that).
    val avgHitPoints get() = hitDice.avg + abilityScores.con.abilityModifier * hitDiceCount

//    /**
//     * Checks if [other] is equal to this.
//     *
//     * Note that equality is based on whether the two objects would conflict
//     * (i.e. their [names][name] would match), not necessarily that all values are equal.
//     */
//    override fun equals(other: Any?) = other is Monster &&
//        name.normalizeForInsensitiveComparisons() == other.name.normalizeForInsensitiveComparisons()
//    override fun hashCode() = name.hashCode()
//    override fun toString() = name
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
    fun matches(monster: Monster) =
        monster.name.normalizeForInsensitiveComparisons().contains(name) &&
            positiveTags.all { positiveTag -> monster.tags.any { it.normalizeForInsensitiveComparisons() == positiveTag } } &&
            monster.tags.all { it.normalizeForInsensitiveComparisons() !in negativeTags }
}

/**
 * Enum for creature sizes.
 */
enum class CreatureSize {
    TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN;

    override fun toString(): String {
        return super.toString().lowercase().replaceFirstChar { it.titlecase() }
    }
}
