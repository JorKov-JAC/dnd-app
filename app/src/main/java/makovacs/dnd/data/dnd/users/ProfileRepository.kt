package makovacs.dnd.data.dnd.users

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun saveProfile(profileData: ProfileData)
    fun getProfile(): Flow<ProfileData>
    suspend fun clear()
}