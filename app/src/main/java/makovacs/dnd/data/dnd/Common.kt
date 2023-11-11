/**
 * Contains classes related to DnD stats.
 */

package makovacs.dnd.data.dnd

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import makovacs.dnd.data.dnd.Dice.Companion.typicalPossibleSides

/**
 * A collection of stats such as strength and dexterity.
 *
 * @param str A strength score.
 * @param dex A dexterity score.
 * @param con A constitution score.
 * @param int A intelligence score.
 * @param wis A wisdom score.
 * @param cha A charisma score.
 */
@Parcelize
data class AbilityScores(val str: Int, val dex: Int, val con: Int, val int: Int, val wis: Int, val cha: Int) :
    Parcelable {
    /**
     * A list of functions which create a new instance with a changed property.
     *
     * Ordering is the same as found in the primary constructor.
     */
    @IgnoredOnParcel
    val abilityCopiers = listOf<(Int) -> AbilityScores>(
        { copy(str = it) },
        { copy(dex = it) },
        { copy(con = it) },
        { copy(int = it) },
        { copy(wis = it) },
        { copy(cha = it) }
    )

    /**
     * The ordered list of ability scores.
     *
     * Ordering is the same as found in the primary constructor.
     */
    @IgnoredOnParcel
    val scoreList = listOf(str, dex, con, int, wis, cha)

    companion object
}

/**
 * A tag which can be applied to a monster.
 *
 * Example: Gnolls would have the "Medium" and "Humanoid" tags.
 *
 * Two instances are considered equal when their [names][name] match.
 *
 * @param name The tag's name.
 * @param description A description of the tag.
 */
data class MonsterTag(val name: String, val description: String?) {
    override fun equals(other: Any?): Boolean = other is MonsterTag && name == other.name
    override fun hashCode(): Int = name.hashCode()
    override fun toString(): String = name
}

// TODO Make a separate Ability value class instead of using Ints?
/**
 * The ability modifier for this ability score.
 *
 * Example: An strength score of 15 gives a modifier of +2.
 */
val Int.abilityModifier get() = (this / 2 - 5).coerceIn(-5, 10)

/**
 * Represents a number of a dice, each with the same number of sides.
 *
 * @param count The number of dice. For example, in 3d6, the count is 3.
 * Must be a positive number.
 * @param sides The number of sides on each dice.
 * For example, in 3d6, the sides are 6.
 * Must be a valid number of sides (see [typicalPossibleSides]).
 * @throws IllegalArgumentException Thrown when [count] or [sides] are invalid.
 */
data class Dice(val count: Int, val sides: Int) {
    init {
        if (count <= 0) throw IllegalArgumentException("Must have at least 1 die.")
        if (sides < 2) throw IllegalArgumentException("Must have at least 2 sides.")
    }

    companion object {
        /**
         * List of sane [sides] values.
         */
        val typicalPossibleSides = listOf(2, 3, 4, 6, 8, 10, 12, 20, 100)

        /**
         * List of good values of [sides] for damage dice.
         * This is a strict subset of [typicalPossibleSides].
         */
        val possibleDamageDiceSides = listOf(4, 6, 8, 10, 12)
    }

    /** Rolls all dice and returns their sum. */
    fun roll() = (1..count).sumOf { (1..sides).random() }

    /** Gets the average value of rolling all of these dice. */
    val avg get() = (sides + 1f) / 2f * count

    override fun toString() = "${count}d$sides"
}

/**
 * Parses this string as [Dice].
 *
 * Example: "4d10" would return a [Dice] with a [Dice.count] of 4 and
 * [Dice.sides] of 10.
 *
 * Surrounding whitespace is ignored.
 *
 * @param validSides A collection of valid values for [Dice.sides]. Values outside that range will
 * cause a [IllegalArgumentException].
 *
 * Typically, this should be [Dice.typicalPossibleSides].
 * @throws IllegalArgumentException Thrown when this string is invalid.
 */
fun String.toDice(validSides: Collection<Int>): Dice {
    // Ensure format is valid
    val regex = "^[0-9]*[dD][0-9]+$".toRegex()
    regex.find(this)
        ?: throw IllegalArgumentException("\"$this\" is invalid dice notation (ex. 4d10 is 4 10-sided dice).")

    // Extract values
    val (countStr, sidesStr) = this.split('d', 'D')
    val count = countStr.ifBlank { "1" }.toInt() // Default to 1 if absent
    val sides = sidesStr.toInt()

    // Validate sides
    if (sides !in validSides) throw IllegalArgumentException("Invalid number of sides: $sides. Must be one of {${validSides.joinToString(", ")}}.")

    // Return instance (may throw IllegalArgumentException):
    return Dice(count, sides)
}
