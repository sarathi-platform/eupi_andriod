package com.nudge.core.usecase.language

import com.nudge.core.BLANK_STRING
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_GET_V3_CONFIG_LANGUAGE
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.data.repository.language.FetchLanguageRepositoryImpl
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.language.LanguageConfigModel
import com.nudge.core.network.ApiException
import javax.inject.Inject

class LanguageConfigUseCase @Inject constructor(
    private val languageRepositoryImpl: FetchLanguageRepositoryImpl,
    apiCallJournalRepository: IApiCallJournalRepository
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {


    suspend fun invoke(
        screenName: String = BLANK_STRING,
        triggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
        moduleName: String = BLANK_STRING,
    ): Boolean {
        try {
            val apiResponse = languageRepositoryImpl.getLanguageV3FromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    languageRepositoryImpl.deleteLanguageDataFromDB()
                    languageRepositoryImpl.saveLanguageDataToDB(it.languageList)
                }
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.SUCCESS.name,
                        customData = mapOf(),
                        errorMsg = BLANK_STRING
                    )
                }

                return true
            } else {
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.FAILED.name,
                        customData = mapOf(),
                        errorMsg = apiResponse.message
                    )
                }
                return false
            }

        } catch (apiException: ApiException) {
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = mapOf(),
                    errorMsg = apiException.stackTraceToString()
                )
            }
            throw apiException
        } catch (ex: Exception) {
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = mapOf(),
                    errorMsg = ex.stackTraceToString()
                )
            }
            throw ex
        }
    }

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        transactionId: String,
        customData: Map<String, Any>
    ): Boolean {
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
        return invoke(
            screenName = screenName,
            triggerType = triggerType,
            moduleName = moduleName
        )
    }

    suspend fun fetchLanguageAPI(): ApiResponseModel<LanguageConfigModel>? {
        try {
            val apiResponse = languageRepositoryImpl.getLanguageV3FromNetwork()
            return if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse
            } else {
                null
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getAllLanguage(): List<LanguageEntity> {
        return languageRepositoryImpl.getAllLanguages()
    }

    suspend fun saveLanguageConfig(langList: List<LanguageEntity>?) {
        langList?.let {
            languageRepositoryImpl.deleteLanguageDataFromDB()
            languageRepositoryImpl.saveLanguageDataToDB(it)
        }
    }

    fun addDefaultLanguage() {
        languageRepositoryImpl.saveLanguageDataToDB(
            listOf(
                LanguageEntity(
                    id = 2,
                    language = "English",
                    langCode = "en",
                    orderNumber = 1,
                    localName = "English"
                ),
                LanguageEntity(
                    1,
                    language = "Hindi",
                    langCode = "hi",
                    orderNumber = 2,
                    localName = "हिंदी"
                ),
                LanguageEntity(
                    3,
                    language = "Bengali",
                    langCode = "bn",
                    orderNumber = 3,
                    localName = "বাংলা"
                ),
                LanguageEntity(
                    4,
                    language = "Assamese",
                    langCode = "as",
                    orderNumber = 4,
                    localName = "অসমীয়া"
                ),
                LanguageEntity(
                    5,
                    language = "Bodo",
                    langCode = "be",
                    orderNumber = 5,
                    localName = "बर'"
                )
            )
        )
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_GET_V3_CONFIG_LANGUAGE
    }
}