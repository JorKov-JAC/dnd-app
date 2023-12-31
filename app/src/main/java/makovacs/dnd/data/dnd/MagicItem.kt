package makovacs.dnd.data.dnd

data class MagicItem(
    var name: String,
    var sourceBook: String,
    var rarity: String,
    var description: String,
    var imageId: Int,
    var damageDice: Dice?,
    var damageType: DamageType

) {

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

enum class DamageType {
    None, Acid, Bludgeoning, Cold, Fire, Force, Lightning, Necrotic, Piercing, Poison, Psychic, Radiant, Slashing, Thunder
}
