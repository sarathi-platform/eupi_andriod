package com.nudge.core.usecase.language

import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.language.FetchLanguageRepositoryImpl
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.language.LanguageConfigModel
import com.nudge.core.network.ApiException
import javax.inject.Inject

class LanguageConfigUseCase @Inject constructor(private val languageRepositoryImpl: FetchLanguageRepositoryImpl) {
    suspend fun invoke(): Boolean {
        try {
            val apiResponse = languageRepositoryImpl.getLanguageV3FromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    languageRepositoryImpl.deleteLanguageDataFromDB()
                    languageRepositoryImpl.saveLanguageDataToDB(it.languageList)
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
}