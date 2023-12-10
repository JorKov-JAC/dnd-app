// Main coding: Jordan

package makovacs.dnd.data.dnd.creatures

/**
 * Enum for creature sizes.
 */
enum class CreatureSize {
    TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN;

    override fun toString(): String {
        return super.toString().lowercase().replaceFirstChar { it.titlecase() }
    }
}
