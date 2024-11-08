package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.UserDetailsResponse

interface FetchSelectionUserDataRepository {

    suspend fun fetchAndSaveUserDetailsAndVillageListFromNetwork(userViewApiRequest: String): Boolean
    suspend fun fetchUseDetailFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse>
    suspend fun fetchLanguage(): List<LanguageEntity>
    fun saveUserDetails(userDetailsResponse: UserDetailsResponse)


}