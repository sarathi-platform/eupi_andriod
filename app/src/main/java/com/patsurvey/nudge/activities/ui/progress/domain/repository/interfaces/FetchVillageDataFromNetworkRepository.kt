package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

interface FetchVillageDataFromNetworkRepository {

    suspend fun fetchStepListForVillageFromNetwork(villageId: Int)
    suspend fun fetchDidiListForVillageFromNetwork(villageId: Int)
    suspend fun fetchTolaListForVillageFromNetwork(villageId: Int)
    suspend fun fetchSavedAnswerForVillageFromNetwork(villageId: Int)

    suspend fun updateVillageDataLoadingStatus(villageId: Int, isDataLoaded: Int)

}