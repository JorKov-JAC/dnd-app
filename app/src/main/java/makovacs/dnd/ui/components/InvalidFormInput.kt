// Main coding: Makena

package makovacs.dnd.ui.components

import android.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Creates a dialog box that displays the passed in error message about invalid input. Can be closed
 * by clicking on the screen behind it or clicking the Close button on the box.
 */

@Composable
fun InvalidFormInput(errorMessage: String) {
    val builder = AlertDialog.Builder(LocalContext.current)
    builder.setTitle("Invalid Input")
    builder.setMessage(errorMessage)
    builder.setNeutralButton("Close") { _, _ -> }

    builder.show()
}
