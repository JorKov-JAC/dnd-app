// Contains common UI-related helper functions

package makovacs.dnd.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext

/**
 * Gets a bitmap from this [DrawableRes] id.
 */
@Composable
fun @receiver:DrawableRes Int.toBitmap(): Bitmap {
    return BitmapFactory.decodeResource(LocalContext.current.resources, this)
}

/**
 * Gets a [BitmapPainter] for this bitmap.
 */
fun Bitmap.toPainter(): BitmapPainter {
    return BitmapPainter(asImageBitmap())
}
