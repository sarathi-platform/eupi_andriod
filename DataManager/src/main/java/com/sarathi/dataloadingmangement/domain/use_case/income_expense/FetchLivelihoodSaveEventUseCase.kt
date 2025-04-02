package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_LIVELIHOOD_SAVE_EVENT
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodSaveEventRepositoryImpl
import javax.inject.Inject

class FetchLivelihoodSaveEventUseCase @Inject constructor(
    private val getLivelihoodSaveEventRepositoryImpl: GetLivelihoodSaveEventRepositoryImpl,
    apiCallJournalRepository: IApiCallJournalRepository
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {
    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                )
            ) {
                return false
            }
            val apiResponse =
                getLivelihoodSaveEventRepositoryImpl.getLivelihoodSaveEventFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    getLivelihoodSaveEventRepositoryImpl.saveLivelihoodSaveEventIntoDb(it)
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

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_LIVELIHOOD_SAVE_EVENT
    }
}