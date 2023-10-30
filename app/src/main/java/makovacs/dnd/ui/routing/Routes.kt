package makovacs.dnd.ui.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import makovacs.dnd.ui.screens.AboutScreen
import makovacs.dnd.ui.screens.ContactScreen
import makovacs.dnd.ui.screens.magicitems.DetailScreen
import makovacs.dnd.ui.screens.magicitems.InputForm
import makovacs.dnd.ui.screens.magicitems.ItemScreen

/**
 * [NavHostController] provider.
 */
val LocalNavHostController = compositionLocalOf<NavHostController> {
    error("There is no provided NavHostController")
}

/**
 * Shows the appropriate page based on [LocalNavHostController].
 */
@Composable
fun Router(modifier: Modifier = Modifier) {
    val navHostController = LocalNavHostController.current

    NavHost(navHostController, startDestination = Route.About.route, modifier = modifier) {
        composable(Route.About.route) {
            AboutScreen()
        }
        composable(Route.Contact.route) {
            ContactScreen()
        }
        composable(Route.Form.route) {
            InputForm()
        }
        composable(Route.ItemsList.route) {
            ItemScreen()
        }
        composable(Route.SingleItem.route) {
            DetailScreen(items = it.arguments?.getString("items") ?: "")
        }
    }
}

/**
 * Parent class of all route classes and objects.
 */
sealed class Route(val route: String) {
    /**
     * Route for [AboutScreen].
     */
    object About : Route("about")
    object Contact : Route("contact")
    object Form : Route("FormRoute")
    object SingleItem : Route("SingleItemRoute/{items}") {
        fun go(items: String) = "SingleItemRoute/$items"
    }
    object ItemsList : Route("ItemsListRoute")
}
