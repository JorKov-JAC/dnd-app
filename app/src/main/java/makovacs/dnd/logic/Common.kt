package makovacs.dnd.logic

import android.icu.text.Normalizer2

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