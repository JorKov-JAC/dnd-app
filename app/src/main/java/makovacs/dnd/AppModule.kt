package makovacs.dnd

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import makovacs.dnd.data.dnd.db.magicitems.MagicItemsRepository
import makovacs.dnd.data.dnd.db.monsters.MonstersRepository
import makovacs.dnd.data.dnd.db.monsters.MonstersRepositoryFirebase
import makovacs.dnd.data.dnd.users.AuthRepository
import makovacs.dnd.data.dnd.users.AuthRepositoryFirebase
//import makovacs.dnd.data.dnd.users.ProfileRepository
//import makovacs.dnd.data.dnd.users.ProfileRepositoryDataStore

class AppModule(
    private val appContext: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    /*
    val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryDataStore(appContext)
    }*/
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(auth)
    }

    val magicItemsRepository: MagicItemsRepository by lazy {
        MagicItemsRepository(FirebaseFirestore.getInstance(), authRepository)
    }

    val monstersRepository: MonstersRepository by lazy {
        MonstersRepositoryFirebase()
    }
}

/*
package makovacs.dnd.data.dnd.users

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PROFILE_DATASTORE = "profile_datastore"
private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = PROFILE_DATASTORE)
class ProfileRepositoryDataStore(private val context: Context) : ProfileRepository {
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
            name = it[NAME] ?: ""
        )
    }

    override suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}

package makovacs.dnd.data.dnd.users

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun saveProfile(profileData: ProfileData)
    fun getProfile(): Flow<ProfileData>
    suspend fun clear()
}

package makovacs.dnd.data.dnd.users

data class ProfileData(
    val name: String = ""
)

 */