package com.patsurvey.nudge.repository

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    prefRepo: PrefRepo,
    val configRepositoryLocal: ConfigRepositoryLocal,
    val userRepository: UserRepositoryLocal
):BaseRepository() {

    suspend fun getConfigurationDetails(): Flow<NetworkResult<List<String>>> {
        return flow {
            emit(safeApiCall { apiInterface.configDetails() })
        }.flowOn(Dispatchers.IO)
    }


    //For testing purpose please delete when implementing you code
    suspend fun createTestDb() {
        configRepositoryLocal.insertVillage(VillageEntity(id = 1, name = "Akauni", is_completed = false, needsToPost = false))
    }

}