// Main coding: Jordan

package makovacs.dnd.data.dnd.common

import com.google.firebase.firestore.Exclude
import makovacs.dnd.data.dnd.common.Dice.Companion.typicalPossibleSides

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
data class Dice(val count: Int = 1, val sides: Int = 2) {
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
    val avg @Exclude get() = (sides + 1f) / 2f * count

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
