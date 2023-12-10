// Main coding: Jordan
// Contains common UI-related helper functions

package makovacs.dnd.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import makovacs.dnd.MyApp
import makovacs.dnd.ui.routing.LocalNavHostController

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
