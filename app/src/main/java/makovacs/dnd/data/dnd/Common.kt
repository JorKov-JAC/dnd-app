/**
 * Main coding: Jordan
 * Contains common DnD classes and functions.
 */

package makovacs.dnd.data.dnd

import androidx.annotation.Size
import com.google.firebase.firestore.Exclude
import makovacs.dnd.data.dnd.Dice.Companion.typicalPossibleSides
import makovacs.dnd.logic.ellipsis

/**
 * Main coding: Jordan
 * A collection of stats such as strength and dexterity.
 *
 * @param str A strength score.
 * @param dex A dexterity score.
 * @param con A constitution score.
 * @param int A intelligence score.
 * @param wis A wisdom score.
 * @param cha A charisma score.
 */
data class AbilityScores(val str: Int, val dex: Int, val con: Int, val int: Int, val wis: Int, val cha: Int) {
    init {
        // Validate ranges
        arrayOf(str, dex, con, int, wis, cha)
            .withIndex()
            .forEach { (i, score) ->
                if (score !in validScoreRange) {
                    throw IllegalArgumentException("${abilityNames[i]} must be within $validScoreRange.")
                }
            }
    }

    companion object {
        /**
         * Creates an [AbilityScores] from an iterable of 6 scores.
         *
         * @param scores The 6 score values to use, in the order found in the primary constructor.
         */
        fun from(@Size(6) scores: Iterable<Int>): AbilityScores {
            val s = scores.iterator()
            return AbilityScores(s.next(), s.next(), s.next(), s.next(), s.next(), s.next())
        }

        /**
         * The range of possible values for any score.
         */
        val validScoreRange = 1..30

        /**
         * The names of each ability.
         *
         * Ordering is the same as found in the primary constructor.
         */
        val abilityNames = arrayOf("Str", "Dex", "Con", "Int", "Wis", "Cha")
    }

    /**
     * A list of functions which create a new instance with a changed property.
     *
     * Ordering is the same as found in the primary constructor.
     */
    val abilityCopiers get() = listOf<(Int) -> AbilityScores>(
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
    val scoreList get() = listOf(str, dex, con, int, wis, cha)
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

/**
 * Sealed class for types representing entries in a list of information.
 */
sealed class InformationEntry

/**
 * A text header.
 *
 * @param text The header's text.
 */
data class Header(val text: String) : InformationEntry() {
    override fun toString() = this.text
}

/**
 * A description.
 *
 * @param title An optional title for the description.
 * @param text A textual description.
 */
data class Description(val title: String? = null, val text: String) : InformationEntry() {
    override fun toString() = if (!title.isNullOrBlank()) title else text.ellipsis(20)
}

/**
 * A separator which delimits sections of information.
 */
object Separator : InformationEntry() {
    override fun toString() = "Separator"
}

/**
 * Enum representing the types of [InformationEntry].
 *
 * @param displayName The name that should be shown to the user.
 */
enum class InformationEntryTypes(val displayName: String) {
    SEPARATOR("Separator"),
    HEADER("Header"),
    DESCRIPTION("Description");

    /**
     * Returns [displayName].
     */
    override fun toString() = displayName
}

/**
 * Contains information represented as a series of [InformationEntry] objects.
 *
 * @param entries The information entries that will make up the new instance.
 * Note that redundant entries (like duplicate separators) will be quietly filtered.
 */
class Information(entries: List<InformationEntry>) {
    val entries: List<InformationEntry>

    init {
        // Quietly remove redundant separators:
        this.entries = entries.asSequence().withIndex()
            .dropWhile { it.value is Separator }
            .filterNot { (index, it) ->
                it is Separator &&
                    // Only keep the last of consecutive separators and drop any at the end
                    entries.getOrElse(index + 1) { Separator } is Separator
            }
            .map { it.value }
            .toList()
    }
}
