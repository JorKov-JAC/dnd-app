package makovacs.dnd.ui.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

/**
 * Contains information for a navigation button.
 *
 * @param routeObject The route object to get the parameterized route from.
 * @param goRoute The route to navigate to when pressed.
 * @param icon The button's icon.
 * @param label The text underneath the icon describing the route.
 */
private data class NavButtonInfo(
    val routeObject: Route,
    val goRoute: String,
    val icon: ImageVector,
    val label: String
)

/**
 * The main scaffolding layout used throughout the app.
 *
 * @param content The content to display within the scaffolding. It is provided with [PaddingValues]
 * which should be used by [Modifier.padding].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(modifier: Modifier = Modifier, content: @Composable (padding: PaddingValues) -> Unit) {
    val navHostController = LocalNavHostController.current

    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar {
                // Info for every nav button
                val navButtonInfos = listOf(
                    NavButtonInfo(
                        Route.MonstersListRoute,
                        Route.MonstersListRoute.route,
                        Icons.Default.List,
                        "Monsters"
                    ),
                    NavButtonInfo(
                        Route.ItemsList,
                        Route.ItemsList.route,
                        Icons.Default.List,
                        "Items List"
                    ),
                    NavButtonInfo(
                        Route.About,
                        Route.About.route,
                        Icons.Default.Info,
                        "About"
                    ),
                    NavButtonInfo(
                        Route.Contact,
                        Route.Contact.route,
                        Icons.Default.Phone,
                        "Contact"
                    ),
                    NavButtonInfo(
                        Route.Account,
                        Route.Account.route,
                        Icons.Default.Person,
                        "Account"
                    )
                )

                // Add the nav buttons
                navButtonInfos.forEach {
                    val isSelected = currentDestination?.hierarchy?.any { dest ->
                        dest.route == it.routeObject.route
                    } ?: false

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { if (!isSelected) navHostController.navigate(it.goRoute) },
                        icon = { Icon(it.icon, "${it.label} page") },
                        label = { Text(it.label) }
                    )
                }
            }
        }
    ) {
        content(it)
    }
}
