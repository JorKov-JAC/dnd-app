package makovacs.dnd.logic

import android.icu.text.Normalizer2

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

/**
 * Returns a "cleaned" version of this string.
 *
 * This includes removing excess whitespace and normalizing unicode under NFC.
 */
fun String.normalizeAndClean() = Normalizer2.getNFCInstance()
    .normalize(this)
    // Remove surrounding whitespace
    .trim()
    // Normalize line breaks
    .replace(Regex("\\R"), "\n")
    // Deduplicate whitespace (replace groups with ASCII space)
    .replace(Regex("\\h{2,}"), " ")

/**
 * Returns a version of this string suitable for comparisons with other strings.
 *
 * This includes standardizing whitespace and normalizing unicode under case-folded NFKC.
 *
 * This string can be safely cleaned by [normalizeAndClean] beforehand.
 */
fun String.normalizeForInsensitiveComparisons() = Normalizer2.getNFKCCasefoldInstance()
    .normalize(this.normalizeAndClean())
    // Replace all horizontal whitespace with ASCII spaces
    .replace(Regex("\\h"), " ")

/**
 * Truncates this string to the given character length and replaces the rest with an ellipsis.
 *
 * Ellipsis are included in the [maxLength].
 *
 * @param maxLength The maximum number of characters the resulting string should take. Must be at
 * least 3.
 */
fun String.ellipsis(maxLength: Int): String {
    return if (this.length > maxLength) "${this.substring(0, maxLength - 3)}..." else this
}

/**
 * Encodes this string with
 * [percent encoding](https://en.wikipedia.org/wiki/Percent-encoding#Percent-encoding_in_a_URI),
 * leaving only 'a'-'z', 'A'-'Z', '0'-'9', '_' and '-' intact.
 */
fun String.strictUriEncode() = this
    .flatMap { c ->
        if (
            c in 'a'..'z'
            || c in 'A'..'Z'
            || c in '0'..'9'
            || c == '_'
            || c == '-'
        ) {
            listOf(c)
        } else {
            // Percent encode the character
            c.toString().toByteArray().flatMap { "%${it.toString(16)}".asIterable() }
        }
    }
    .joinToString("")
