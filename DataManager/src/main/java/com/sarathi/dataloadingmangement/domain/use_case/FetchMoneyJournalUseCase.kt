package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ApiStatus
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_MONEY_JOURNAL_DETAILS
import com.sarathi.dataloadingmangement.repository.MoneyJournalNetworkRepository
import javax.inject.Inject


class FetchMoneyJournalUseCase @Inject constructor(
    private val moneyJournalNetworkRepository: MoneyJournalNetworkRepository,
    apiCallJournalRepository: IApiCallJournalRepository
) :
    BaseApiCallNetworkUseCase(apiCallJournalRepository) {

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
                    customData = mapOf(),
                )
            ) {
                return false
            }
            val apiResponse = moneyJournalNetworkRepository.getMoneyJournalFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    moneyJournalNetworkRepository.saveMoneyJournalIntoDb(it)
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
                    customData = mapOf(),
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

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_MONEY_JOURNAL_DETAILS
    }
}