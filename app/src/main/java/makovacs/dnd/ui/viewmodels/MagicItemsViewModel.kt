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

class MagicItemsViewModel(private val magicItemsRepository: MagicItemsRepository) : ViewModel() {
    private var _magicItems = mutableListOf<MagicItem>()
    private val _magicItemsDb = MutableStateFlow(listOf<MagicItem>())
    val magicItemsDb: StateFlow<List<MagicItem>> = _magicItemsDb.asStateFlow()

    val magicItems: List<MagicItem>
        get() = _magicItemsDb.value


    fun remove(item: MagicItem) {
        //_magicItems.remove(item)

    }

    fun removeByName(name: String) {
        _magicItems.removeIf { item -> item.name == name }
    }

    fun getByName(name: String): MagicItem? {
        var magicItem = _magicItems.find { item -> item.name == name }
        return magicItem?.copy()
    }

    fun add(
        name: String,
        source: String,
        rarity: String,
        description: String,
        pictureId: String,
        damageDice: Dice?,
        damageType: DamageType
    ) {
        _magicItems.add(
            MagicItem(
                name,
                source,
                rarity,
                description,
                pictureId,
                damageDice,
                damageType
            )
        )
    }

    fun getAllItems() {
        viewModelScope.launch {
            magicItemsRepository.getMagicItems().collect { magicItems ->
                _magicItemsDb.value = magicItems
            }

        }
    }

    fun addItem(
        name: String,
                source: String,
                rarity: String,
                description: String,
                pictureId: String,
                damageDice: Dice?,
                damageType: DamageType)
    {
        viewModelScope.launch {
           magicItemsRepository.saveMagicItem(MagicItem(name, source, rarity, description, pictureId, damageDice, damageType))

        }
    }

}

class MagicItemsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MagicItemsViewModel(MyApp.appModule.magicItemsRepository) as T
    }
}
