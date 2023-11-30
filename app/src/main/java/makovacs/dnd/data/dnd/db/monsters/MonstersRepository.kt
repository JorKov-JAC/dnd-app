package makovacs.dnd.data.dnd.db.monsters

import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery

interface MonstersRepository {
    suspend fun addMonster(monster: Monster)
    suspend fun getMonster(name: String): Monster?

    /**
     * Gets all monsters which at least match the provided query.
     * **It may return monsters which do not match the query.**
     *
     * @param query The query to use.
     */
    suspend fun queryMonsters(query: MonsterQuery): List<Monster>
}