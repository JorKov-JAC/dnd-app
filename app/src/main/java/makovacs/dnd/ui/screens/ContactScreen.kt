// Main coding: Makena & Jordan

package makovacs.dnd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import makovacs.dnd.R

/**
 * A dummy screen meant to display contact information (phone, email and address) for a company
 * along with a form that could be used to send a message. The actual information in the form is
 * never used.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen() {
    val context = LocalContext.current
    val image = painterResource(id = R.drawable.contactbg)

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

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

                    Row(modifier = Modifier.padding(15.dp)) {
                        TextField(
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(1f),
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") }

                        )

                        TextField(
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(1f),
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") }
                        )
                    }

                    TextField(
                        modifier = Modifier
                            .padding(horizontal = 19.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }

                    )

                    Row(modifier = Modifier.padding(15.dp)) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Message") }

                        )
                    }

                    Button(
                        {
                            // Simulate sending a message
                            // (no, the message isn't actually sent)
                            name = ""
                            email = ""
                            phone = ""
                            message = ""

                            Toast
                                .makeText(
                                    context,
                                    "Your message has been \"received\".",
                                    Toast.LENGTH_SHORT
                                ).show()
                        },
                        // Can only send if all fields have content:
                        enabled = listOf(name, email, phone, message).all { it.isNotBlank() },
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(8.dp)
                    ) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }
}
