package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse

interface IUserDetailRepository {
    suspend fun fetchUseDetailFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse>
    suspend fun fetchLanguage(): List<LanguageEntity>
    fun saveUserDetails(userDetailsResponse: UserDetailsResponse)

}