package makovacs.dnd.ui.viewmodels

import androidx.lifecycle.ViewModel
import makovacs.dnd.data.dnd.DamageType
import makovacs.dnd.data.dnd.Dice
import makovacs.dnd.data.dnd.MagicItem

class MagicItemsViewModel : ViewModel() {
    private val _magicItems = mutableListOf<MagicItem>()
    val magicItems: List<MagicItem>
        get() = _magicItems

    fun remove(item: MagicItem) {
        _magicItems.remove(item)
    }

    fun removeByName(name: String) {
        _magicItems.removeIf { item -> item.name == name }
    }

    fun getByName(name: String): MagicItem? {
        var magicItem = _magicItems.find { item -> item.name == name }
        return magicItem?.copy()
    }

    fun add(name: String, source: String, rarity: String, description: String, pictureId: Int, damageDice: Dice?, damageType: DamageType) {
        _magicItems.add(MagicItem(name, source, rarity, description, pictureId, damageDice, damageType))
    }
}
