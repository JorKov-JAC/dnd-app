// Main coding: Jordan

package makovacs.dnd.ui.components.common.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a list of items and lets the user select one by tapping on it.
 *
 * @param items The items to display.
 * @param selectedIndex The index of the selected item.
 * @param setSelectedIndex Called when the user tries to change the selection to
 * the given index.
 * @param key Converts an item to a unique key.
 * This is necessary if [itemContent] needs to persist state when [items] is modified.
 * @param listState The state of the internal lazy list.
 * @param itemContent The component used to display a given item.
 */
@Composable
fun <T> SelectableList(
    items: List<T>,
    selectedIndex: Int?,
    setSelectedIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    listState: LazyListState = rememberLazyListState(),
    itemContent: @Composable (T) -> Unit
) {
    LaunchedEffect(selectedIndex) {
        // Make sure item is visible
        if (
            selectedIndex != null &&
            !listState.layoutInfo.visibleItemsInfo.any { it.index == selectedIndex }
        ) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .then(modifier)
    ) {
        itemsIndexed(items, key = key?.let { { _, item -> it(item) } }) { index, it ->
            var cardModifier = Modifier.clickable { setSelectedIndex(index) }

            // Change background of selected item
            if (selectedIndex == index) {
                cardModifier = cardModifier
                    .background(MaterialTheme.colorScheme.tertiary)
            }

            cardModifier = cardModifier.padding(8.dp)

            OutlinedCard(modifier = cardModifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    itemContent(it)
                }
            }
        }
    }
}
