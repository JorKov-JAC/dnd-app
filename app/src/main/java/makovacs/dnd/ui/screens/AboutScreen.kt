package makovacs.dnd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R

/**
 * The about screen which describes the app and its developers.
 */
@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Image(
                    painterResource(id = R.drawable.dnd_logo),
                    "The D&D logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(maxWidth = 250.dp, maxHeight = 250.dp)
                )

                Text(
                    "About Us",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text("We are a pair of passionate gamers who enjoy Dungeons & Dragons and the vast world of possibilities it presents. We're creating this app to make that world and its information more accessible.\n\nMakena enjoys playing D&D with their friends and loves the recent Baldur's Gate 3.\n\nJordan has always admired D&D from a distance but has only played once and it went terribly. They are scarred for life.")
            }
        }
    }
}
