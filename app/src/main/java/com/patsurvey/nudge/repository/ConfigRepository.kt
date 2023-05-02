package com.patsurvey.nudge.repository

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    prefRepo: PrefRepo
):BaseRepository() {

    suspend fun getConfigurationDetails(): Flow<NetworkResult<List<String>>> {
        return flow {
            emit(safeApiCall { apiInterface.configDetails() })
        }.flowOn(Dispatchers.IO)
    }

}