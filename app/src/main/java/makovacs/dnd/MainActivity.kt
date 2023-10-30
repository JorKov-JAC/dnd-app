package makovacs.dnd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import makovacs.dnd.ui.layouts.MainLayout
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Router
import makovacs.dnd.ui.screens.magicitems.rememberMutableStateListOf
import makovacs.dnd.ui.theme.AppTheme
// temp, remove after viewmodel intergrated
val MagicItems = compositionLocalOf<SnapshotStateList<String>> { SnapshotStateList() }
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // temp, remove after viewmodel intergrated
                    val magicItems = rememberMutableStateListOf<String>()

                    CompositionLocalProvider(MagicItems provides magicItems) {
                    }

                    val navController = rememberNavController()

                    CompositionLocalProvider(
                        LocalNavHostController provides navController
                    ) {
                        MainLayout {
                            Router(modifier = Modifier.padding(it))
                        }
                    }
                }
            }
        }
    }
}
