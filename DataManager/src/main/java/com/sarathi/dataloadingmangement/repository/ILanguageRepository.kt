package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
import com.sarathi.dataloadingmangement.network.response.ConfigResponseModel

interface ILanguageRepository {
    suspend fun fetchLanguageDataFromServer(): ApiResponseModel<ConfigResponseModel?>
    suspend fun saveLanguageData(languages: List<LanguageEntity>)
}