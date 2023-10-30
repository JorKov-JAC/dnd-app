package makovacs.dnd.ui.routing

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import makovacs.dnd.ui.routing.Route.Companion.NAME_KEY
import makovacs.dnd.ui.screens.AboutScreen
import makovacs.dnd.ui.screens.ContactScreen
import makovacs.dnd.ui.screens.monsters.MonsterDetailsScreen
import makovacs.dnd.ui.screens.monsters.MonstersListScreen
import makovacs.dnd.ui.screens.monsters.NewMonsterScreen
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel

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

        composable(Route.MonstersListRoute.route) {
            MonstersListScreen()
        }

        composable(Route.MonsterDetailsRoute.route) {
            val monstersVm = LocalMonstersViewModel.current
            val name = it.arguments!!.getString(NAME_KEY)!! // Must have a name
            MonsterDetailsScreen(monster = monstersVm.getMonster(name))
        }

        composable(Route.NewMonsterRoute.route) {
            val monstersVm = LocalMonstersViewModel.current
            NewMonsterScreen(onSubmit = monstersVm::addMonster)
        }
    }
}

/**
 * Parent class of all route classes and objects.
 */
sealed class Route(val route: String) {
    companion object {
        /** Key in the route arguments for a name. */
        const val NAME_KEY = "name"
    }

    /**
     * Route for [AboutScreen].
     */
    object About : Route("about")
    object Contact : Route("contact")

    /** Route for [MonstersListScreen]. */
    object MonstersListRoute: Route("monsters")

    /** Route for [MonsterDetailsScreen]. */
    object MonsterDetailsRoute: Route("monster/{$NAME_KEY}") {
        /**
         * Creates a route string with the given arguments.
         * @param name The name of the monster the page is for.
         */
        fun go(name: String) = "monster/${Uri.encode(name)}"
    }

    /** Route for [NewMonsterScreen]. */
    object NewMonsterRoute: Route("monsters/new")
}
