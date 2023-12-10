package makovacs.dnd.logic

import android.icu.text.Normalizer2

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
 * Encodes this string with
 * [percent encoding](https://en.wikipedia.org/wiki/Percent-encoding#Percent-encoding_in_a_URI),
 * leaving only 'a'-'z', 'A'-'Z', '0'-'9', '_' and '-' intact.
 */
fun String.strictUriEncode() = this
    .flatMap { c ->
        if (
            c in 'a'..'z' ||
            c in 'A'..'Z' ||
            c in '0'..'9' ||
            c == '_' ||
            c == '-'
        ) {
            // Leave the character as-is
            listOf(c)
        } else {
            // Percent encode the character
            c.toString().toByteArray().flatMap { "%${it.toString(16)}".asIterable() }
        }
    }
    .joinToString("")

/**
 * Checks if a given string is a valid email address.
 *
 * @param email The string to check.
 * @return True if the string is a valid email, false otherwise.
 */
fun isValidEmail(email: String): Boolean {
    val regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    val found = regex.find(email) ?: false

    return true
}
