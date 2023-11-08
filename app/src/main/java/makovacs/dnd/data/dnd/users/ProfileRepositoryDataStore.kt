package makovacs.dnd.data.dnd.users

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PROFILE_DATASTORE ="profile_datastore"
private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = PROFILE_DATASTORE)
class ProfileRepositoryDataStore (private val context: Context) : ProfileRepository  {
    companion object {
        val NAME = stringPreferencesKey("NAME")
    }

    override suspend fun saveProfile(profileData: ProfileData) {
        context.dataStore.edit {
            it[NAME] = profileData.name
        }
    }

    override fun getProfile(): Flow<ProfileData> = context.dataStore.data.map {
        ProfileData(
            name = it[NAME] ?: "",
        )
    }

    override suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

}


