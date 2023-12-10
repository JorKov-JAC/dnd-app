package makovacs.dnd.data.dnd.monsters

import makovacs.dnd.logic.normalizeForInsensitiveComparisons

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
                .filter { it.isNotEmpty() }
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
