// Main coding: Makena

package makovacs.dnd.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import makovacs.dnd.MyApp
import makovacs.dnd.data.dnd.DamageType
import makovacs.dnd.data.dnd.Dice
import makovacs.dnd.data.dnd.MagicItem
import makovacs.dnd.data.dnd.db.magicitems.MagicItemsRepository
/**
 * ViewModel for the magic items created in the app and stored in the repository
 * @property magicItemsRepository The connection to the repository of magic items
 */
class MagicItemsViewModel(private val magicItemsRepository: MagicItemsRepository) : ViewModel() {
    private val _magicItems = MutableStateFlow(listOf<MagicItem>())
    val magicItemsDb: StateFlow<List<MagicItem>> = _magicItems.asStateFlow()

    // the list containing the magic items
    val magicItems: List<MagicItem>
        get() = _magicItems.value

    // takes the name of the item and deletes all items owned by the user with the same name
    fun removeByName(name: String) {
        viewModelScope.launch {
            magicItemsRepository.delete(name)
            // gets all the items to update the db
            getAllItems()
        }
    }

    // gets a copy of a magic item with the name matching the passed in string
    fun getByName(name: String): MagicItem? {
        var magicItem = _magicItems.value.find { item -> item.name == name }
        return magicItem?.copy()
    }

    // gets all the items corresponding to the signed in user and puts them in the list
    fun getAllItems() {
        viewModelScope.launch {
            magicItemsRepository.getMagicItems().collect { magicItems ->
                _magicItems.value = magicItems
            }
        }
    }

    // adds a magic item with the specified values into the user's magic item collection
    fun addItem(
        name: String,
        source: String,
        rarity: String,
        description: String,
        pictureId: String,
        damageDice: Dice?,
        damageType: DamageType
    ) {
        viewModelScope.launch {
            magicItemsRepository.saveMagicItem(MagicItem(name, source, rarity, description, pictureId, damageDice, damageType))
        }
    }

    // deletes all the user's magic items
    fun deleteAll() {
        viewModelScope.launch {
            magicItemsRepository.deleteAll()
        }
    }
}

class MagicItemsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MagicItemsViewModel(MyApp.appModule.magicItemsRepository) as T
    }
}
