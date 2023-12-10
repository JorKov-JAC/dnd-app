package makovacs.dnd.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R

/*
    A not currently functional screen meant to display contact information (phone, email and address)
    for a company along with a form that could be used to send a message. The fields do not update and
    the send button is disabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen() {
    val image = painterResource(id = R.drawable.contactbg)
    val placeholder: String = ""
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(image, contentScale = ContentScale.FillBounds)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
        ) {
            Card {
                Text(
                    "Contact Us",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Card(modifier = Modifier.padding(horizontal = 10.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.padding(15.dp)) {
                        Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone icon")
                        Text("555-555-5555")
                    }
                    Row(modifier = Modifier.padding(15.dp)) {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "Email icon")
                        Text("dndapp@fake.com")
                    }
                    Row(modifier = Modifier.padding(15.dp)) {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = "Home icon")
                        Text("24 Dnd Street, Neverwinter, 123 456, Forgotten Realms")
                    }
                }
            }
        }
    }
}
