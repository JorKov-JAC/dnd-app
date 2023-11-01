package makovacs.dnd.data

import androidx.lifecycle.ViewModel


class MagicItemViewModel : ViewModel() {
    private val _magicItems = mutableListOf<MagicItem>()
    val magicItems: List<MagicItem>
        get() = _magicItems


    fun remove(item: MagicItem) {
        _magicItems.remove(item)
    }

    fun removeByName(name: String)
    {
        _magicItems.removeIf { item -> item.name == name }
    }

    fun getByName(name: String) : MagicItem?
    {
        var magicItem = _magicItems.find { item -> item.name == name }
        return magicItem?.copy()
    }

    fun add(name: String, source: String, rarity: String, description: String, pictureId: Int, damageDice:String, damageType: DamageType)
    {
        _magicItems.add(MagicItem(name, source, rarity, description, pictureId, damageDice, damageType))
    }
}

