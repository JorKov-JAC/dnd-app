// Main coding: Makena

package makovacs.dnd.data.dnd.magicItems

import makovacs.dnd.data.dnd.common.Dice

/**
 * Creates an instance of a MagicItem object
 *
 * @property name The name of the item
 * @property sourceBook The source of the item (ie. name of book or self made)
 * @property rarity The rarity of the item (ie. rare, uncommon, wondrous)
 * @property description The description of the item
 * @property damageDice The amount of damage the item can do
 * @property damageType The type of damage the item can do
 *
 */
data class MagicItem(
    var name: String = "empty",
    var sourceBook: String = "empty",
    var rarity: String = "empty",
    var description: String = "empty",
    var image: String = "R.drawable.dndmisc",
    var damageDice: Dice? = null,
    var damageType: DamageType = DamageType.None

) {

    // Returns a string with the magic item's information formatted
    override fun toString(): String {
        return if (damageDice != null) {
            "Name: $name \nSource: $sourceBook \nRarity: $rarity \nDescription: $description \n" +
                "Damage Type: ${damageType.name} \nDamage Dice: $damageDice"
        } else {
            "Name: $name \nSource: $sourceBook \nRarity: $rarity \nDescription: $description \n" +
                "Damage Type: ${damageType.name}"
        }
    }
}
