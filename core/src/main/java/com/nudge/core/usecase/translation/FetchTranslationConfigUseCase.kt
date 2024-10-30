package com.nudge.core.usecase.translation

import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.translation.FetchTranslationRepositoryImpl
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.network.ApiException
import javax.inject.Inject

class FetchTranslationConfigUseCase @Inject constructor(private val translationRepositoryImpl: FetchTranslationRepositoryImpl) {
    suspend fun invoke(): Boolean {
        try {
            val apiResponse = translationRepositoryImpl.getTranslationFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    translationRepositoryImpl.saveTranslationDataToDB(it)
                }
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getTranslationsConfig(): List<TranslationConfigEntity>? {
        return translationRepositoryImpl.getTranslationsConfig()
    }
}
