package com.nudge.core.data.repository.language

import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.language.LanguageConfigModel

interface IFetchLanguageRepository {
    suspend fun getLanguageV3FromNetwork(): ApiResponseModel<LanguageConfigModel>
    fun saveLanguageDataToDB(languageList: List<LanguageEntity>)
    suspend fun deleteLanguageDataFromDB()
    suspend fun getAllLanguages(): List<LanguageEntity>

}