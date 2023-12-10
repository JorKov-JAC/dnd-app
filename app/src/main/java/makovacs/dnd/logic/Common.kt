// Main coding: Jordan

package makovacs.dnd.logic

import java.util.Date

/**
 * Generates a new universally unique identifier.
 *
 * Resulting UIDs are url- and filepath-safe.
 * Sorting UIDs generated from this function by ASCII code will give a chronological ordering
 * relative to the system time of the machine which generated them.
 */
fun generateUid(): String {
    val bitsPerChar = 6
    val timeChars = 7 // 42 bits for the time
    val targetLength = 16 // 96 bits total
    val sb = StringBuilder(targetLength)

    // Each character represents 6 bits
    val orderedChars = (listOf('-') + ('0'..'9') + ('A'..'Z') + '_' + ('a'..'z'))

    // First we encode the time in base64url
    var timeBits = Date().time shl (Long.SIZE_BITS - timeChars * bitsPerChar)

    // For the first few chars, encode the current time
    for (i in 1..timeChars) {
        val sixBits = timeBits ushr (Long.SIZE_BITS - bitsPerChar) // Get the first char's bits
        sb.append(orderedChars[sixBits.toInt()]) // Add the char corresponding to those bits
        timeBits = timeBits shl bitsPerChar // Shift out the first char
    }

    // Keep adding random chars until the target length is met
    while (sb.length < targetLength) {
        sb.append(orderedChars.random())
    }

    return sb.toString()
}
