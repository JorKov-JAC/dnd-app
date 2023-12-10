package makovacs.dnd.logic

/**
 * Swaps two items in a list.
 *
 * @param a The index of the first item.
 * @param b The index of the second item.
 */
fun <T> MutableList<T>.swap(a: Int, b: Int) {
    val temp = this[a]
    this[a] = this[b]
    this[b] = temp
}
