package makovacs.dnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import makovacs.dnd.R
import makovacs.dnd.logic.swap
import kotlin.math.roundToInt

/**
 * A dialog which displays its contents within a card.
 *
 * @param onDismissRequest Called when the user attempts to dismiss the dialog.
 * @param content The content to display within the dialog.
 */
@Composable
fun CardDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(modifier = modifier) {
            Box(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

/**
 * Shows a confirmation dialog for deleting something.
 *
 * @param onCloseDialog Called when the user attempts to close the dialog, whether that's because
 * they dismissed it or they deleted the item.
 * @param itemStr A string representing the item being deleted, or null to use a
 * generic deletion message.
 * @param onDelete Called when the user confirms the deletion.
 */
@Composable
fun ConfirmDeleteDialog(
    onCloseDialog: () -> Unit,
    itemStr: String?,
    onDelete: () -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val itemStr = if (itemStr == null) {
        stringResource(R.string.confirm_deletion_generic_item_name)
    } else {
        stringResource(R.string.quoted_format, itemStr)
    }

    AlertDialog(
        onDismissRequest = onCloseDialog,
        confirmButton = @Composable {
            Button({
                onDelete()
                onCloseDialog()
            }) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = @Composable {
            Button(onCloseDialog) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = @Composable {
            Icon(Icons.Default.Warning, contentDescription = null /* Decorative */)
        },
        title = @Composable {
            Text(stringResource(R.string.confirm_deletion_title))
        },
        text = @Composable {
            Text(stringResource(R.string.confirm_deletion_body_format, itemStr))
        }
    )
}

/**
 * Allows the user to select an item using a dropdown.
 *
 * @param choices The list of selectable values.
 * @param setValue Called when the user selects the given choice.
 * @param reset Provides a "reset" option to the user to reset the selection. If null, no such
 * option is given.
 * @param choiceName Converts an item from [choices] into a displayable name.
 * @param choiceIcon Converts an item from [choices] into an icon. If null, no icon is shown.
 * @param content The content which can be clicked to open the dropdown.
 */
@Composable
fun <T : Any> DropdownSelector(
    choices: Iterable<T>,
    setValue: (index: Int, value: T) -> Unit,
    reset: (() -> Unit)?,
    choiceName: (T) -> String,
    modifier: Modifier = Modifier,
    choiceIcon: (@Composable (T) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.clickable { dropdownExpanded = !dropdownExpanded }) {
        content()

        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            if (reset != null) {
                DropdownMenuItem(
                    text = { Text("Reset", fontStyle = FontStyle.Italic) },
                    onClick = {
                        reset()
                        dropdownExpanded = false
                    }
                )
            }

            choices.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(choiceName(item)) },
                    onClick = {
                        setValue(index, item)
                        dropdownExpanded = false
                    },
                    trailingIcon = choiceIcon?.let { { choiceIcon(item) } }
                )
            }
        }
    }
}

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
            val maxColumnCount = (constraints.maxWidth / maxPlaceableWidth).coerceIn(1, measurables.size)

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

/**
 * A [DropdownSelector] for items which are displayed to the user as strings.
 *
 * @param choices The list of selectable values.
 * @param value The currently selected value.
 * @param setValue Called when the user selects the given choice.
 * @param reset Provides a "reset" option to the user to reset the selection. If null, no such
 * option is given.
 * @param label A label to put before the selector.
 * @param choiceName Converts an item from [choices] into a displayable name.
 * If null, defaults to [Any.toString].
 */
@Composable
fun <T : Any> StringDropdownSelector(
    choices: Iterable<T>,
    value: T?,
    setValue: (index: Int, value: T) -> Unit,
    reset: (() -> Unit)?,
    label: String?,
    modifier: Modifier = Modifier,
    choiceName: (T) -> String = { it.toString() }
) {
    Row(modifier = modifier) {
        if (label != null) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .align(CenterVertically)
            )
        }
        DropdownSelector(
            choices = choices,
            setValue = setValue,
            reset = reset,
            choiceName = choiceName,
            modifier = Modifier.weight(1f)
        ) {
            Card {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(value?.let { choiceName(it) } ?: "", modifier = Modifier.widthIn(min = 32.dp).weight(1f))
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null /* Decorative */,
                        modifier = Modifier.align(CenterVertically).padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

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

/**
 * Displays a list and allows the user to modify it.
 *
 * @param items The list of items to display.
 * @param onSwap Called when the user swaps two items.
 * @param key Converts an item to a unique key.
 * This is necessary if [itemContent] needs to persist state when [items] is modified.
 * @param selectedIndexState A state representing the currently selected item, or null if there is
 * no selected item.
 * @param onCreateDialogContent Component for creating a new item,
 * or null if the user cannot create items.
 * Is given the index at which the new item should be inserted.
 * Should call onDone when an item has finished being added.
 * @param onDelete Called when the user tries to delete an item with the given index,
 * or null if the user cannot delete items.
 * @param itemToName Gets an item's user-visible name.
 * @param onEditDialogContent Component for editing an item,
 * or null if the user cannot edit items.
 * Is given the item and its index.
 * Should call onDone when an item has finished being edited.
 * @param editingEnabled Called with the currently selected item and its index.
 * If it returns true and [onEditDialogContent] is not null, the item can be editing.
 * Defaults to always be true.
 * @param title Optional title to display.
 * @param itemContent Converts an item into a component.
 */
@Composable
fun <T> EditableList(
    items: List<T>,
    onSwap: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    // The only way for callers to *optionally* interact with the selected index is to expose a
    // state for it. Having a value and a setter instead would force the caller to provide them:
    selectedIndexState: MutableState<Int?> = rememberSaveable {
        mutableStateOf(
            null
        )
    },
    onCreateDialogContent: (@Composable (insertionIndex: Int, onDone: () -> Unit) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    itemToName: ((T) -> String)? = null,
    onEditDialogContent: (@Composable (itemIndex: Int, item: T, onDone: () -> Unit) -> Unit)? = null,
    editingEnabled: (index: Int, item: T) -> Boolean = { _, _ -> true },
    title: String? = null,
    itemContent: @Composable (T) -> Unit
) {
    var selectedIndex by selectedIndexState

    Column(modifier = modifier) {
        // Header
        // TODO This condition is fragile and may break if we forget to update
        //      it when adding new header items, replace with something cleaner:
        if (title != null || onCreateDialogContent != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            ) {
                // Title
                if (title != null) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // "Create" button
                if (onCreateDialogContent != null) {
                    var createDialogEnabled by rememberSaveable {
                        mutableStateOf(
                            false
                        )
                    }
                    IconButton(onClick = { createDialogEnabled = true }) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(R.string.create),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    if (createDialogEnabled) {
                        CardDialog(
                            onDismissRequest = { createDialogEnabled = false }
                        ) {
                            val insertionIndex =
                                selectedIndex?.let { it + 1 } ?: items.size

                            onCreateDialogContent(insertionIndex) {
                                createDialogEnabled = false
                                selectedIndex = insertionIndex
                            }
                        }
                    }
                }
            }
        }

        // Display items
        SelectableList(
            items = items,
            key = key,
            selectedIndex = selectedIndex,
            setSelectedIndex = { selectedIndex = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            itemContent = itemContent
        )

        // Mutation buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .fillMaxWidth()
        ) {
            // Up button
            IconButton(
                enabled = selectedIndex?.let { it > 0 } ?: false,
                onClick = {
                    onSwap(selectedIndex!!, selectedIndex!! - 1)
                    selectedIndex = selectedIndex!! - 1
                }
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    stringResource(R.string.move_up)
                )
            }

            // Down button
            IconButton(
                enabled = selectedIndex?.let { it < items.size - 1 } ?: false,
                onClick = {
                    onSwap(selectedIndex!!, selectedIndex!! + 1)
                    selectedIndex = selectedIndex!! + 1
                }
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    stringResource(R.string.move_down)
                )
            }

            // Edit button
            if (onEditDialogContent != null) {
                var editDialogEnabled by rememberSaveable { mutableStateOf(false) }
                IconButton(
                    enabled = selectedIndex != null &&
                        editingEnabled(selectedIndex!!, items[selectedIndex!!]),
                    onClick = { editDialogEnabled = true }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        stringResource(R.string.edit)
                    )
                }

                if (editDialogEnabled) {
                    CardDialog(
                        onDismissRequest = { editDialogEnabled = false }
                    ) {
                        onEditDialogContent(
                            selectedIndex!!,
                            items[selectedIndex!!]
                        ) { editDialogEnabled = false }
                    }
                }
            }

            // Delete button
            // TODO Confirmation prompt
            if (onDelete != null) {
                var deleteConfirmationVisible by rememberSaveable { mutableStateOf(false) }

                IconButton(
                    enabled = selectedIndex != null,
                    onClick = {
                        deleteConfirmationVisible = true
                    }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        stringResource(R.string.delete)
                    )
                }

                if (deleteConfirmationVisible) {
                    ConfirmDeleteDialog(
                        onCloseDialog = { deleteConfirmationVisible = false },
                        itemStr = itemToName?.invoke(items[selectedIndex!!])
                    ) {
                        onDelete(selectedIndex!!)
                        selectedIndex = null
                    }
                }
            }
        }
    }
}

/**
 * An [EditableList] which handles a list of strings.
 *
 * @param mutableList The list to use and mutate.
 * @param singleLine True if the strings must be single-line (no line breaks), false otherwise.
 * @param title Optional title to display.
 * @param validator Returns null if the given string is a valid value, or an error message
 * otherwise.
 * @param cleaner Modifies strings before they are added or edited.
 * Occurs before the string is validated and uniqueness is checked.
 * @param uniqueOn If provided, strings will be guaranteed to be unique.
 * This function is called on the strings, and then the results are compared with each other to
 * ensure they are all unique. For example, if this function were [String.lowercase], then "apple"
 * and "Apple" would conflict with each other even if they aren't exactly the same.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableStringList(
    // This component is just a convenience wrapper around EditableList;
    // To keep things simple, we interact with a mutable list directly:
    mutableList: SnapshotStateList<String>,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
    title: String? = null,
    validator: ((String) -> String?)? = null,
    cleaner: ((String) -> String)? = null,
    uniqueOn: ((String) -> Any)? = null
) {
    @Suppress("NAME_SHADOWING")
    val validator = validator ?: { null } // Default to allow everything (null = no errors)
    @Suppress("NAME_SHADOWING")
    val cleaner = cleaner ?: { it } // Default to leaving the string as-is

    val alreadyExistsFormat = stringResource(R.string.already_exists_format)

    EditableList(
        title = title,
        modifier = Modifier
            .height(300.dp)
            .then(modifier),
        items = mutableList,
        key = uniqueOn,
        onSwap = mutableList::swap,
        onCreateDialogContent = { insertionIndex, onDone ->
            var str by rememberSaveable { mutableStateOf("") }
            var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

            Column {
                TextField(str, onValueChange = { str = it }, singleLine = singleLine)

                if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

                Button(onClick = {
                    str = cleaner(str)

                    // Validate the new value
                    errorMsg = validator(str)
                    if (errorMsg != null) return@Button

                    // Ensure uniqueness
                    if (uniqueOn != null) {
                        val comparableStr = uniqueOn(str)
                        val preexisting = mutableList.find { other -> comparableStr == uniqueOn(other) }
                        if (preexisting != null) {
                            errorMsg = alreadyExistsFormat.format(preexisting)
                            return@Button
                        }
                    }

                    mutableList.add(insertionIndex, str)
                    onDone()
                }) {
                    Text(stringResource(R.string.create))
                }
            }
        },
        onEditDialogContent = { itemIndex, item, onDone ->
            var str by rememberSaveable { mutableStateOf(item) }
            var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

            Column {
                TextField(str, onValueChange = { str = it }, singleLine = singleLine)

                if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

                Button(onClick = {
                    str = cleaner(str)

                    // Validate the new value
                    errorMsg = validator(str)
                    if (errorMsg != null) return@Button

                    // Ensure uniqueness
                    if (uniqueOn != null) {
                        val comparableStr = uniqueOn(str)
                        val preexisting = mutableList.find { other -> comparableStr == uniqueOn(other) }
                        if (
                            preexisting != null &&
                            preexisting != item // Ignore conflicts with itself
                        ) {
                            errorMsg = alreadyExistsFormat.format(preexisting)
                            return@Button
                        }
                    }

                    mutableList[itemIndex] = str
                    onDone()
                }) {
                    Text(stringResource(R.string.save))
                }
            }
        },
        onDelete = {
            mutableList.removeAt(it)
        },
        itemToName = { it }
    ) {
        Text(it)
    }
}

/**
 * An input field for ints that can be empty.
 *
 * @param value The current value.
 * @param setValue Called when the user wants to change [value] to the provided value.
 * @param label The label shown on the input field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NullableIntField(
    value: Int?,
    setValue: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value?.toString() ?: "",
        {
            if (it.isBlank()) {
                setValue(null)
            } else {
                it.toIntOrNull()?.let(setValue)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}
