// Main coding: Jordan

package makovacs.dnd.ui.components.common.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Searches [items] for items that match [query].
 *
 * @param items The items to search through.
 * @param query The query to use in [mapper].
 * @param mapper Performs both filtering and sorting of items.
 * It is called with [query] and an individual item, and should return null if the item does not fit
 * [query], otherwise should return a [Comparable] object which can be used to sort valid items.
 * @return The ordered list of matching items, or null if a search is ongoing.
 */
@Composable
fun <T, Q, R : Comparable<R>> search(
    items: Iterable<T>,
    query: Q,
    mapper: (query: Q, item: T) -> R?
): List<T>? {
    // Use remember because we can just recalculate it
    val results = remember { MutableStateFlow<List<T>?>(null) }

    LaunchedEffect(items, query, mapper) {
        // Debounce (don't start searching until changes have momentarily stopped)
        delay(250)

        // Empty results while searching
        results.update { null }

        // Search
        results.update {
            items.map { it to mapper(query, it) } // Pair each item with its comparable
                .filter { it.second != null } // Filter out non-matches
                .sortedBy { it.second } // Sort matches
                .map { it.first } // Get the original items out of the pairs
        }
    }

    return results.collectAsState().value
}
