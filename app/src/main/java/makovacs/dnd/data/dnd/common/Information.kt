// Main coding: Jordan

package makovacs.dnd.data.dnd.common

import makovacs.dnd.logic.ellipsis

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
