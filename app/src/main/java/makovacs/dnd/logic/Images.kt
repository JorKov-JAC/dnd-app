package makovacs.dnd.logic

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Main coding: Jordan
 * Scales this bitmap to fit within [maxWidth] and [maxHeight] while maintaining its aspect ratio.
 *
 * @param maxWidth The maximum possible width of the resulting image.
 * @param maxHeight The maximum possible height of the resulting image.
 * @param filter True if filtering should be used when scaling.
 */
fun Bitmap.fit(maxWidth: Int, maxHeight: Int, filter: Boolean = true): Bitmap {
    val aspectRatio = width.toFloat() / height
    val newWidth = (min(maxHeight, height) * aspectRatio)
        .roundToInt()
        .coerceIn(1, maxWidth)
    val newHeight = (newWidth / aspectRatio).roundToInt().coerceIn(1, maxHeight)

    return scale(newWidth, newHeight, filter)
}
