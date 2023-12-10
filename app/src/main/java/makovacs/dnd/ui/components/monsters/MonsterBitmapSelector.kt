package makovacs.dnd.ui.components.monsters

import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R
import makovacs.dnd.ui.components.common.dropdowns.DropdownSelector
import makovacs.dnd.ui.util.toBitmap
import makovacs.dnd.ui.util.toPainter

/**
 * Lets the user select a bitmap for a monster.
 *
 * @param bitmap The current bitmap, or null if there is none.
 * @param setBitmap Called when the user selects a bitmap.
 * Is passed in the bitmap image as the first argument or null if the user chooses to have no
 * bitmap, and an optional description of the image as the second argument.
 */
@Composable
fun MonsterBitmapSelector(bitmap: Bitmap?, setBitmap: (Bitmap?, String?) -> Unit, modifier: Modifier = Modifier) {
    // TODO This is gross, decodes image on every recompose:
    val defaultBitmap = R.drawable.no_img.toBitmap()

    val context = LocalContext.current

    /** Activity launcher used to get an image via an implicit GET_CONTENT intent. */
    val getImageActivityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it == null) return@rememberLauncherForActivityResult

        // getBitmap is deprecated, but alternatives seem to require a higher SDK version
        @Suppress("DEPRECATION")
        setBitmap(
            MediaStore.Images.Media.getBitmap(context.contentResolver, it),
            null /* Cannot assume image description */
        )
    }

    val imageOfFormat = stringResource(R.string.imageOf_format)

    Card(modifier = modifier) {
        DropdownSelector(
            listOf(
                null to "Custom", // Get image from phone
                R.drawable.gnolls.toBitmap() to stringResource(id = R.string.gnolls),
                R.drawable.wolf.toBitmap() to stringResource(id = R.string.wolf),
                R.drawable.imp.toBitmap() to stringResource(id = R.string.imp)
            ),
            setValue = { _, it ->
                if (it.first == null) {
                    // No image signifies the "Custom" option, get an image from the phone:
                    getImageActivityLauncher.launch("image/*")
                } else {
                    setBitmap(it.first, imageOfFormat.format(it.second))
                }
            },
            reset = { setBitmap(null, null) },
            choiceName = { it.second },
            choiceIcon = {
                it.first?.let { bitmap ->
                    Image(
                        bitmap.toPainter(),
                        imageOfFormat.format(it.second),
                        modifier = Modifier.sizeIn(maxWidth = 150.dp, maxHeight = 150.dp)
                    )
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = (bitmap ?: defaultBitmap).toPainter(),
                contentDescription = null, // Useless unless we implement user-provided descriptions
                modifier = Modifier.size(200.dp)
            )

            Icon(
                Icons.Default.ArrowDropDown,
                stringResource(id = R.string.newMonster_imageDropdownIcon),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
