// Main coding: Jordan

package makovacs.dnd.data.dnd.creatures

import androidx.annotation.Size

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
