package com.nudge.core.usecase.translation

import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_FETCH_TRANSLATIONS
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.api.ApiJournalDatabaseRepositoryImpl
import com.nudge.core.data.repository.translation.FetchTranslationRepositoryImpl
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.network.ApiException
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class FetchTranslationConfigUseCase @Inject constructor(
    private val translationRepositoryImpl: FetchTranslationRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val apiJournalDatabaseRepository: ApiJournalDatabaseRepositoryImpl,
) : BaseApiCallNetworkUseCase() {
    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
                return false
            }
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

    override fun getApiEndpoint(): String {
        return SUB_PATH_FETCH_TRANSLATIONS
    }
}
