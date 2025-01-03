package com.nudge.core.data.repository.translation

import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.TranslationModel

interface IFetchTranslationRepository {
    suspend fun getTranslationFromNetwork(): ApiResponseModel<List<TranslationModel>>
    suspend fun saveTranslationDataToDB(translation: List<TranslationModel>)
    suspend fun deleteTranslationDataFromDB()
    suspend fun getTranslationAsPerKeyDataFromDB(key: String): TranslationConfigEntity?
    suspend fun getTranslationsConfig(): List<TranslationConfigEntity>?
}