package makovacs.dnd.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

/**
 * An adaptive grid in which every cell has the same width.
 *
 * @param content The content to display in the grid. Every component will be centered in its own
 * cell.
 */
@Composable
fun EvenWidthGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }

        val placeables = measurables.map { it.measure(Constraints()) }

        val maxPlaceableWidth = placeables.maxOf { it.width }
        assert(maxPlaceableWidth != Constraints.Infinity) { "Tried to put content with infinite width into an EvenGrid" }

        val columnCount = if (maxPlaceableWidth > 0) {
            // Need to coerce in case we have too little width for the max placeable width
            // (minimum of 1 item per row) or infinite space (max columns = total number of items)
            val maxColumnCount =
                (constraints.maxWidth / maxPlaceableWidth).coerceIn(1, measurables.size)

            // (x - 1) / y + 1 is equivalent to ceil(x/y)
            val rowCount = (placeables.size - 1) / maxColumnCount + 1

            // Result: Smallest number of columns that keeps the smallest row count
            (placeables.size - 1) / rowCount + 1
        } else {
            placeables.size // Width of all content is 0, just use one row
        }

        // We normally end up with more space in each column than the max placeable width, so this
        // is the actual space each column has
        val columnWidth = if (constraints.maxWidth == Constraints.Infinity) {
            maxPlaceableWidth.toFloat() // We have infinite space, so use a sane value
        } else {
            constraints.maxWidth.toFloat() / columnCount
        }

        // Actual dimensions this component uses
        val actualWidth = (columnWidth * columnCount).roundToInt()
        val actualHeight = placeables
            .chunked(columnCount) // Into rows
            .sumOf { row -> row.maxOf { it.height } } // Max height of each row, added up
        assert(actualHeight != Constraints.Infinity) { "Tried to put content with infinite height into an EvenGrid" }

        layout(actualWidth, actualHeight) {
            var y = 0
            placeables.chunked(columnCount).forEachIndexed { rowIndex, row ->
                val rowHeight = row.maxOf { it.height }

                row.forEachIndexed { columnIndex, placeable ->
                    placeable.placeRelative(
                        // Placeables are centered in their individual cells, hence the
                        // "+ (length - placeableLength) / 2"
                        x = (columnIndex * columnWidth + (columnWidth - placeable.width) / 2).roundToInt(),
                        y = (y + (rowHeight.toFloat() - placeable.height) / 2).roundToInt()
                    )
                }

                y += rowHeight
            }
        }
    }
}
