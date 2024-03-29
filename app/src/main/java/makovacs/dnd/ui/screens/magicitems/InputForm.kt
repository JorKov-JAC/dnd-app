// Main coding: Makena

package makovacs.dnd.ui.screens.magicitems

import android.app.AlertDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import makovacs.dnd.data.dnd.common.Dice
import makovacs.dnd.data.dnd.common.toDice
import makovacs.dnd.data.dnd.magicItems.DamageType
import makovacs.dnd.ui.components.common.InvalidFormInput
import makovacs.dnd.ui.routing.LocalNavHostController
import makovacs.dnd.ui.routing.Route

/**
 *
 * Provides an input form for a user to input the name, source book, rarity and description of a Dungeons and Dragons
 * magic item. Contains two buttons, one to add the item to the list of items, and the other to clear the
 * current item information from the text fields. Adding an item brings up a a more detailed page about the item
 * that also allows the user to delete it.
 * Does not allow for underscores or empty fields to be inputted, and calls the composable InvalidFormInput to
 * inform the user they cannot do whichever they were attempting with a passed in error message.
 *
 * @param add The function to create a magic item and add it to the list of them
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm(add: (String, String, String, String, String, Dice?, DamageType) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var sourceBook by rememberSaveable { mutableStateOf("") }
    var rarity by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var damageDiceStr by rememberSaveable { mutableStateOf("") }
    var damageType by rememberSaveable { mutableStateOf(DamageType.None) }
    val navController = LocalNavHostController.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        var invalidInput by rememberSaveable { mutableStateOf(false) }
        var errorMessage by rememberSaveable { mutableStateOf("") }

        Row {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                value = name,
                singleLine = true,
                onValueChange = { name = it },
                label = { Text("Item name") },
                placeholder = { Text("ex. Bag of Holding") }
            )
        }
        Row {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                value = sourceBook,
                singleLine = true,
                onValueChange = { sourceBook = it },
                label = { Text("Item source") },
                placeholder = { Text("ex. PHB") }
            )

            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                value = rarity,
                singleLine = true,
                onValueChange = { rarity = it },
                label = { Text("Item rarity") },
                placeholder = { Text("ex. Uncommon") }
            )
        }
        Row {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                value = description,
                onValueChange = { description = it },
                label = { Text("Item description") },
                placeholder = { Text("ex. A bag that can hold anything") }

            )
        }

        Spacer(
            modifier = Modifier
                .height(5.dp)
        )

        val pictures = listOf(
            "dndaxe",
            "dndbag",
            "dndbook",
            "dndbow",
            "dndpotion",
            "dndshield",
            "dndmisc"
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.padding(2.dp)
        ) {
            Text("Type of Magic Item Image", modifier = Modifier.padding(3.dp))

            var expanded by remember { mutableStateOf(false) }
            val items = listOf("Axe", "Bag", "Book", "Bow", "Potion", "Shield", "Other")

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary))
                    .padding(1.dp)
            ) {
                Text(
                    items[selectedIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded = true })
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                ) {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
                        }, text = { Text(s) })
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(5.dp)
        )

        Row {
            TextField(
                modifier = Modifier
                    .weight(1f),
                value = damageDiceStr,
                onValueChange = { damageDiceStr = it },
                label = { Text("(Optional) Damage Dice") },
                placeholder = { Text("ex. 2d8") }

            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            ) {
                Text("Damage type", modifier = Modifier.padding(3.dp))

                var expandedDamage by remember { mutableStateOf(false) }
                val damages = DamageType.values()
                var indexDamage = 0

                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary))
                        .padding(1.dp)
                ) {
                    Text(
                        damageType.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expandedDamage = true })
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer
                            )
                    )
                    DropdownMenu(
                        expanded = expandedDamage,
                        onDismissRequest = {
                            expandedDamage = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer
                            )
                    ) {
                        damages.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                indexDamage = index
                                damageType = s
                                expandedDamage = false
                            }, text = { Text(s.name) })
                        }
                    }
                }
            }
        }

        Row {
            val drawable = pictures[selectedIndex]

            Button(onClick = {
                if (name == "" || rarity == "" || sourceBook == "" || description == "") {
                    invalidInput = true
                    errorMessage = "No boxes may be empty."
                    return@Button
                }

                // Get dice
                var damageDice: Dice? = null

                if (damageDiceStr.isNotBlank()) {
                    damageDice = try {
                        damageDiceStr.toDice(Dice.possibleDamageDiceSides)
                    } catch (ex: Exception) {
                        invalidInput = true
                        errorMessage = ex.message ?: "Invalid dice."
                        return@Button
                    }
                }

                add(name, sourceBook, rarity, description, drawable, damageDice, damageType)
                navController.navigate(Route.SingleItem.go(name))
            }) {
                Text("Add Item")
            }
            Button(onClick = {
                name = ""
                rarity = ""
                description = ""
                sourceBook = ""
                damageDiceStr = ""
                selectedIndex = 0
                damageType = DamageType.None
            }) {
                Text("Clear Item Information")
            }
        }

        Spacer(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
        )

        if (invalidInput) {
            invalidInput = false
            InvalidFormInput(errorMessage)
        }
    }
}

/*
 * Creates a dialog box that displays the passed in error message about invalid input. Can be closed
 * by clicking on the screen behind it or clicking the Close button on the box.
 */

@Composable
fun InvalidFormInput(errorMessage: String) {
    val builder = AlertDialog.Builder(LocalContext.current)
    builder.setTitle("Invalid Input")
    builder.setMessage(errorMessage)
    builder.setNeutralButton("Close") { _, _ -> }

    builder.show()
}
