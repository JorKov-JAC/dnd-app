package makovacs.dnd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import makovacs.dnd.ui.layouts.MainLayout
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Router
import makovacs.dnd.ui.theme.AppTheme
import makovacs.dnd.ui.viewmodels.LocalMonstersViewModel
import makovacs.dnd.ui.viewmodels.MonstersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO Temporary(?)
            val defaultInitializedViewModel = MonstersViewModel().initializeToDefaultData()
            val monstersVm = viewModel {
                defaultInitializedViewModel
            }

            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    CompositionLocalProvider(
                        LocalNavHostController provides navController,
                        LocalMonstersViewModel provides monstersVm
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
