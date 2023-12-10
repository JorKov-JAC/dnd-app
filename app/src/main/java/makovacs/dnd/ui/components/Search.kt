package makovacs.dnd.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Main coding: Jordan
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

/**
 * A list of items with a search box at the top.
 *
 * @param items The items to search through.
 * @param queryStr The query string to use. This is converted by [queryModifier] before being used.
 * @param setQueryStr Called when the user tries to set [queryStr] to the given string.
 * @param queryModifier Takes a query string and converts it to a query object that is then passed
 * to [mapper].
 * @param mapper Performs both filtering and sorting of items.
 * It is called with the query object from [queryModifier] and an individual item,
 * and should return null if the item does not fit the query,
 * otherwise should return a [Comparable] object which can be used to sort valid items.
 * @param key Converts an item into a unique key. Necessary if [itemContent] needs to preserve state
 * when the items are reordered.
 * @param label Custom label text for the search query text box.
 * @param itemContent Component displayed for each item. It is called with the item's search result
 * index and the item itself.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T, Q, R : Comparable<R>> StringSearchList(
    items: List<T>,
    queryStr: String,
    setQueryStr: (String) -> Unit,
    queryModifier: (String) -> Q,
    mapper: (query: Q, item: T) -> R?,
    key: ((item: T) -> Any)?,
    modifier: Modifier = Modifier,
    label: String = "Query",
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    val searchResults = search(items = items, query = queryModifier(queryStr), mapper = mapper)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .then(modifier)
    ) {
        // Query input
        TextField(
            queryStr,
            setQueryStr,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (searchResults == null) {
            // Show loading text...
            Text("Searching", modifier = Modifier.align(CenterHorizontally))
        } else {
            // Done searching, show results
            if (searchResults.isEmpty()) {
                Text("No results.", modifier = Modifier.align(CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier) {
                    itemsIndexed(
                        searchResults,
                        key = key?.let { { _, item -> key(item) } }
                    ) { index, item -> itemContent(index, item) }
                }
            }
        }
    }
}
