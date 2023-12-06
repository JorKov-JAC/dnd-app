package makovacs.dnd.data.dnd

data class MagicItem(
    var name: String = "empty",
    var sourceBook: String = "empty",
    var rarity: String = "empty",
    var description: String = "empty",
    var image: String = "R.drawable.dndmisc",
    var damageDice: Dice? = null,
    var damageType: DamageType = DamageType.None

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
