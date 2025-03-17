package com.nudge.core.usecase.translation

import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.translation.FetchTranslationRepositoryImpl
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.network.ApiException
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class FetchTranslationConfigUseCase @Inject constructor(private val translationRepositoryImpl: FetchTranslationRepositoryImpl) {
    suspend fun invoke(): Boolean {
        try {
            val startTime = System.currentTimeMillis()
            val apiResponse = translationRepositoryImpl.getTranslationFromNetwork()
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "FetchTranslationConfigUseCase :/registry-service/translations/fetch : ${System.currentTimeMillis() - startTime}"
            )
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    translationRepositoryImpl.saveTranslationDataToDB(it)
                }
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchTranslationConfigUseCase: ${System.currentTimeMillis() - startTime}"
                )

                return true
            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchTranslationConfigUseCase: ${System.currentTimeMillis() - startTime}"
                )
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
