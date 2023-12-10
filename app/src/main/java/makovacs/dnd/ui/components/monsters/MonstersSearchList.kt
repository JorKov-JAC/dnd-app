// Main coding: Jordan

// Contains components specific to DnD monsters (which are a subcategory of creatures)

package makovacs.dnd.ui.components.monsters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.data.dnd.monsters.MonsterQuery
import makovacs.dnd.ui.components.common.ForwardArrow
import makovacs.dnd.ui.components.common.search.StringSearchList

/**
 * Displays a list of Monsters.
 *
 * @param monsters The monsters to display.
 * @param onClick Called when the user clicks on a Monster. Null if nothing should happen.
 * @param queryStr The string to use as a search query.
 * @param setQueryStr Called when the user tries to set [queryStr] to the provided value.
 */
@Composable
fun MonstersSearchList(
    monsters: List<Monster>,
    onClick: ((Monster) -> Unit)?,
    queryStr: String,
    setQueryStr: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StringSearchList(
        items = monsters,
        queryStr = queryStr,
        setQueryStr = setQueryStr,
        queryModifier = MonsterQuery.Companion::fromString,
        mapper = { query, monster -> if (query.matches(monster)) monster.name else null },
        label = "Query (ex: \"Gnoll +Humanoid -Small\")",
        key = { it.id },
        modifier = Modifier
            .padding(4.dp)
            .then(modifier)
    ) { _, it ->
        Box {
            var cardModifier: Modifier = Modifier

            // Add clicking if a callback was provided
            // (can't use "enabled" for accessibility reasons)
            if (onClick != null) cardModifier = cardModifier.clickable { onClick(it) }

            MonsterCard(it, cardModifier)

            if (onClick != null) {
                ForwardArrow(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}
