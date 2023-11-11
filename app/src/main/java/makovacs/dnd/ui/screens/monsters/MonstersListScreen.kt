package makovacs.dnd.ui.screens.monsters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.logic.normalizeForInsensitiveComparisons
import makovacs.dnd.ui.components.MonsterCard
import makovacs.dnd.ui.components.StringSearchList
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel

/**
 * Displays the list of [monsters][Monster] from [LocalMonstersViewModel].
 */
@Composable
fun MonstersListScreen(modifier: Modifier = Modifier) {
    val navHostController = LocalNavHostController.current
    val monstersVm = LocalMonstersViewModel.current

    Box(modifier = modifier.fillMaxSize()) {
        MonstersList(
            monsters = monstersVm.monsters,
            onClick = {
                navHostController.navigate(Route.MonsterDetailsRoute.go(it.name))
            },
            onDelete = { monstersVm.removeMonster(it.name) }
        )
        Button(
            onClick = { navHostController.navigate(Route.NewMonsterRoute.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Text("+")
        }
    }
}

/**
 * Displays a list of Monsters.
 *
 * @param monsters The monsters to display.
 * @param onClick Called when the user clicks on a Monster. Null if nothing should happen.
 * @param onDelete Called when the user tries to delete a Monster. Null if user cannot delete
 * monsters.
 */
@Composable
fun MonstersList(
    monsters: List<Monster>,
    onClick: ((Monster) -> Unit)?,
    onDelete: ((Monster) -> Unit)?,
    modifier: Modifier = Modifier
) {
    StringSearchList(
        items = monsters,
        queryModifier = MonsterQuery::fromString,
        mapper = { query, monster -> if (query.matches(monster)) monster.name else null },
        label = "Query (ex: \"Gnoll +Humanoid -Small\")",
        key = { it.id },
        modifier = Modifier.padding(4.dp).then(modifier)
    ) { _, it ->
        Box {
            var cardModifier: Modifier = Modifier

            // Add clicking if a callback was provided
            // (can't use "enabled" for accessibility reasons)
            if (onClick != null) cardModifier = cardModifier.clickable { onClick(it) }

            MonsterCard(it, cardModifier)

            // Add delete button if a callback was provided
            if (onDelete != null) {
                IconButton(
                    onClick = { onDelete(it) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, "Delete \"${it.name}\"")
                }
            }
        }
    }
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
