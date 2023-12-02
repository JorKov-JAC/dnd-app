package makovacs.dnd.ui.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController.Companion.KEY_DEEP_LINK_INTENT
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.coroutines.runBlocking
import makovacs.dnd.ui.routing.Route.Companion.NAME_KEY
import makovacs.dnd.ui.routing.Route.Companion.QUERY_KEY
import makovacs.dnd.ui.screens.AboutScreen
import makovacs.dnd.ui.screens.Account
import makovacs.dnd.ui.screens.ContactScreen
import makovacs.dnd.ui.screens.SignIn
import makovacs.dnd.ui.screens.SignUp
import makovacs.dnd.ui.screens.magicitems.DetailScreen
import makovacs.dnd.ui.screens.magicitems.InputForm
import makovacs.dnd.ui.screens.magicitems.ItemScreen
import makovacs.dnd.ui.screens.monsters.EditMonsterScreen
import makovacs.dnd.ui.screens.monsters.MonsterDetailsScreen
import makovacs.dnd.ui.screens.monsters.MonstersListScreen
import makovacs.dnd.ui.screens.monsters.MonstersSelectScreen
import makovacs.dnd.ui.screens.monsters.NewMonsterScreen
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel
import makovacs.dnd.ui.viewmodels.MagicItemsViewModel

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
fun Router(modifier: Modifier = Modifier, magicItemsVM: MagicItemsViewModel = viewModel()) {
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

        composable(
            Route.MonstersSelectRoute.route,
            deepLinks = Route.MonstersSelectRoute.deepLinks
        ) { navEntry ->
            val localContext = LocalContext.current

            MonstersSelectScreen(initialQuery = navEntry.arguments?.getString(QUERY_KEY) ?: "") { monster ->
                @Suppress("DEPRECATION")
                val resultIntent = navEntry.arguments?.get(KEY_DEEP_LINK_INTENT) as Intent?

                // True if this route was launched by another activity:
                if (resultIntent?.action != null && localContext is Activity) {
                    // We were called by another activity, return result and end this activity
                    resultIntent.putExtra("name", monster.name)
                    resultIntent.putExtra("description", monster.descriptionOrDefault)

                    localContext.setResult(Activity.RESULT_OK, resultIntent)
                    localContext.finish()
                } else {
                    // Not called by another activity, just go to the monster's page
                    navHostController.navigate(Route.MonsterDetailsRoute.go(monster.name))
                }
            }
        }

        composable(Route.MonsterDetailsRoute.route) {
            val monstersVm = LocalMonstersViewModel.current
            val name = it.arguments!!.getString(NAME_KEY)!! // Must have a name
            val monster = monstersVm.getMonster(name)
            MonsterDetailsScreen(
                monster = monster,
                onDelete = { monster?.let { monstersVm.removeMonster(monster) } }
            )
        }

        composable(Route.NewMonsterRoute.route) {
            val monstersVm = LocalMonstersViewModel.current

            NewMonsterScreen {
                runBlocking { monstersVm.addMonster(it) }

                // Go to the new monster's page
                navHostController.popBackStack()
                navHostController.navigate(Route.MonsterDetailsRoute.go(it.name))
            }
        }

        composable(Route.EditMonsterRoute.route) {
            /**
             * True if the monster was *just* edited, in which case we shouldn't do anything since
             * the monster might have been renamed. Just wait for navigation to kick in.
             */
            var updateOccurred by rememberSaveable { mutableStateOf(false) }
            if (updateOccurred) return@composable

            val monstersVm = LocalMonstersViewModel.current
            val name = it.arguments!!.getString(NAME_KEY)!! // Must have a name
            val monster = monstersVm.getMonster(name)

            if (monster == null) {
                // Monster doesn't exist; this might happen if the monster is renamed/deleted at the
                // same time. This would be really rare, we'll just pop back out.
                navHostController.popBackStack()

                return@composable
            }

            EditMonsterScreen(monster) { oldMonster, newMonster ->
                monstersVm.updateMonster(oldMonster, newMonster)
                updateOccurred = true

                // Go to the monster's page
                navHostController.popBackStack()
                navHostController.navigate(Route.MonsterDetailsRoute.go(newMonster.name))
            }
        }

        composable(Route.ItemForm.route) {
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
                name = it.arguments!!.getString("name")!!,
                magicItemsVM::removeByName,
                magicItemsVM::getByName
            )
        }
        composable(Route.SignUp.route) {
            SignUp()
        }
        composable(Route.SignIn.route) {
            SignIn()
        }
        composable(Route.Account.route) {
            Account()
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

        /** Key in the route arguments for a query. */
        const val QUERY_KEY = "query"
    }

    /**
     * Route for [AboutScreen].
     */
    object About : Route("about")
    object Contact : Route("contact")

    /** Route for [MonstersListScreen]. */
    object MonstersListRoute : Route("monsters")

    /** Route for [MonstersSelectScreen]. */
    object MonstersSelectRoute : Route("monsters/select/{$QUERY_KEY}") {
        /** Deep links for this route. */
        val deepLinks = listOf(navDeepLink { uriPattern = "dnd://monster/?query={$QUERY_KEY}" })

        /**
         * Creates a route string with the given arguments.
         * @param query The initial query to use to search for a monster.
         */
        fun go(query: String = "") = "monsters/select/${Uri.encode(query)}"
    }

    /** Route for [MonsterDetailsScreen]. */
    object MonsterDetailsRoute : Route("monster/{$NAME_KEY}") {
        /**
         * Creates a route string with the given arguments.
         * @param name The name of the monster the page is for.
         */
        fun go(name: String) = "monster/${Uri.encode(name)}"
    }

    /** Route for [NewMonsterScreen]. */
    object NewMonsterRoute : Route("monsters/new")

    /** Route for [EditMonsterScreen]. */
    object EditMonsterRoute : Route("monsters/edit/{name}") {
        fun go(name: String) = "monsters/edit/${Uri.encode(name)}"
    }

    object ItemForm : Route("ItemFormRoute")
    object SingleItem : Route("SingleItemRoute/{name}") {
        fun go(name: String) = "SingleItemRoute/$name"
    }
    object ItemsList : Route("ItemsListRoute")

    object SignIn : Route("SignIn")

    object SignUp : Route("SignUp")

    object Account : Route("Account")
}
