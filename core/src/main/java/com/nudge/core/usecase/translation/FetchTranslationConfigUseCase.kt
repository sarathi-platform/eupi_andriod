package com.nudge.core.usecase.translation

import com.nudge.core.BLANK_STRING
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_FETCH_TRANSLATIONS
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.data.repository.translation.FetchTranslationRepositoryImpl
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.network.ApiException
import javax.inject.Inject

class FetchTranslationConfigUseCase @Inject constructor(
    private val translationRepositoryImpl: FetchTranslationRepositoryImpl,
    apiCallJournalRepository: IApiCallJournalRepository
) :
    BaseApiCallNetworkUseCase(apiCallJournalRepository) {
    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        transactionId: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    transactionId = transactionId,
                    customData = mapOf(),
                )
            ) {
                return false
            }
            val apiResponse = translationRepositoryImpl.getTranslationFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    translationRepositoryImpl.saveTranslationDataToDB(it)
                }
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = mapOf(),
                    errorMsg = BLANK_STRING
                )
                return true
            } else {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = apiResponse.message
                )
                return false
            }

        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = mapOf(),
                errorMsg = apiException.stackTraceToString()
            )
            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = mapOf(),
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
    }

    suspend fun getTranslationsConfig(): List<TranslationConfigEntity>? {
        return translationRepositoryImpl.getTranslationsConfig()
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_FETCH_TRANSLATIONS
    }
}
