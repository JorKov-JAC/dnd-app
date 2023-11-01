package makovacs.dnd.ui.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import makovacs.dnd.data.MagicItem
import makovacs.dnd.data.MagicItemViewModel
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
fun Router(modifier: Modifier = Modifier, magicItemsVM: MagicItemViewModel = viewModel()) {
    val navHostController = LocalNavHostController.current

    NavHost(navHostController, startDestination = Route.About.route, modifier = modifier) {
        composable(Route.About.route) {
            AboutScreen()
        }
        composable(Route.Contact.route) {
            ContactScreen()
        }
        composable(Route.Form.route) {
            InputForm(magicItemsVM::add)
        }
        composable(Route.ItemsList.route) {
            ItemScreen(
                magicItemsVM.magicItems,
                magicItemsVM::removeByName,
                magicItemsVM::getByName
            )
        }
        composable(Route.SingleItem.route) {
            DetailScreen(
                name = it.arguments?.getString("name") ?: "",
                magicItemsVM::removeByName,
                magicItemsVM::getByName
            )
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
    object SingleItem : Route("SingleItemRoute/{name}") {
        fun go(name: String) = "SingleItemRoute/$name"
    }
    object ItemsList : Route("ItemsListRoute")
}
