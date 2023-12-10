package makovacs.dnd.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route
import makovacs.dnd.ui.util.getCurrentUser

/**
 * Displays [content] if the user is signed-in, or a button telling them to sign-in if not.
 *
 * @param signInMessage The text telling the user to sign in.
 * @param content The content to display if the user is signed in.
 */
@Composable
fun SignInButtonOrElse(
    signInMessage: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val user = getCurrentUser()
    val navHostController = LocalNavHostController.current

    if (user == null) {
        Button(
            { navHostController.navigate(Route.SignIn.route) },
            modifier = modifier
        ) {
            Text(signInMessage)
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}
