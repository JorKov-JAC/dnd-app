// Main coding: Jordan

package makovacs.dnd.data.dnd.db.monsters

import kotlinx.coroutines.flow.Flow
import makovacs.dnd.data.dnd.monsters.Monster
import makovacs.dnd.data.dnd.monsters.MonsterQuery

/**
 * A repository for [Monster] entries.
 */
interface MonstersRepository {
    /**
     * Adds a monster to this repository.
     *
     * @param monster The monster to add.
     */
    suspend fun addMonster(monster: Monster)

    /**
     * Removes a monster from this repository.
     *
     * @param monster The monster to remove.
     */
    suspend fun deleteMonster(monster: Monster)

    /**
     * Gets all monsters which at least match the provided query.
     * **This may return monsters which do not match the query.**
     *
     * @param query The query to use.
     */
    fun getAtLeast(query: MonsterQuery): Flow<List<Monster>?>

    /**
     * Gets the monster with the given [Monster.id], or null if it does not exist.
     *
     * @param id The [Monster.id] of the monster to get.
     */
    suspend fun getMonster(id: String): Flow<Monster?>

    /**
     * Updates a monster.
     *
     * @param oldMonster The monster's old data.
     * @param newMonster The monster's new data. Its [Monster.id] should match that of [oldMonster].
     */
    suspend fun updateMonster(oldMonster: Monster, newMonster: Monster)
}
