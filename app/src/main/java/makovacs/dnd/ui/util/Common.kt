// Contains common UI-related helper functions

package makovacs.dnd.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import makovacs.dnd.MyApp
import makovacs.dnd.ui.routing.LocalNavHostController

/**
 * Main coding: Jordan
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

/**
 * Pops the [LocalNavHostController]'s back stack once.
 *
 * Use this inside of guard cases instead of [NavHostController.popBackStack] since that may pop the
 * stack multiple times.
 */
@Composable
fun popBackStackOnce() {
    var alreadyPopped by rememberSaveable { mutableStateOf(false) }
    if (alreadyPopped) return
    alreadyPopped = true

    val navHostController = LocalNavHostController.current
    navHostController.popBackStack()
}

/**
 * Gets the current user.
 */
@Composable
fun getCurrentUser() = MyApp
    .appModule
    .authRepository
    .currentUser()
    .collectAsState()
    .value
