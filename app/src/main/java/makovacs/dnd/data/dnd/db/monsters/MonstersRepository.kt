package makovacs.dnd.data.dnd.db.monsters

import makovacs.dnd.data.dnd.Monster

interface MonstersRepository {
	fun addMonster(monster: Monster)
}