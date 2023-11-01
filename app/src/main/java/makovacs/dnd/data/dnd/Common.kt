/**
 * Contains classes related to DnD stats.
 */

package makovacs.dnd.data.dnd

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

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
