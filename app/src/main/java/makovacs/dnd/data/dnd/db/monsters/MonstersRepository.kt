package makovacs.dnd.data.dnd.db.monsters

import kotlinx.coroutines.flow.Flow
import makovacs.dnd.data.dnd.Monster
import makovacs.dnd.data.dnd.MonsterQuery

interface MonstersRepository {
    suspend fun addMonster(monster: Monster)
    suspend fun getMonster(name: String): Flow<Monster?>

    /**
     * Gets all monsters which at least match the provided query.
     * **This may return monsters which do not match the query.**
     *
     * @param query The query to use.
     */
    fun queryMonsters(query: MonsterQuery): Flow<List<Monster>?>

    suspend fun deleteMonster(monster: Monster)

    suspend fun updateMonster(oldMonster: Monster, newMonster: Monster)
}