package makovacs.dnd.ui.components.creatures

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import makovacs.dnd.R
import makovacs.dnd.ui.util.toBitmap
import makovacs.dnd.ui.util.toPainter

/**
 * Displays a creature's image.
 *
 * @param imageBitmap The bitmap of the image to display.
 * @param imageDesc The optional description of the image. Must be null if
 * [imageBitmap] is null.
 */
@Composable
fun CreatureImage(imageBitmap: Bitmap?, imageDesc: String?, modifier: Modifier = Modifier) {
    @Suppress("NAME_SHADOWING")
    val imageDesc = imageDesc ?: stringResource(R.string.genericMonsterImageDesc)

    Image(
        painter = (imageBitmap ?: R.drawable.no_img.toBitmap()).toPainter(),
        contentDescription = imageDesc,
        modifier = modifier
    )
}
