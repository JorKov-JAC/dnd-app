// Main coding: Jordan

package makovacs.dnd.ui.layouts

import androidx.compose.ui.graphics.vector.ImageVector
import makovacs.dnd.ui.routing.Route

/**
 * Contains information for a navigation button.
 *
 * @param routeObject The route object to get the parameterized route from.
 * @param goRoute The route to navigate to when pressed.
 * @param icon The button's icon.
 * @param label The text underneath the icon describing the route.
 */
data class NavButtonInfo(
    val routeObject: Route,
    val goRoute: String,
    val icon: ImageVector,
    val label: String
)
