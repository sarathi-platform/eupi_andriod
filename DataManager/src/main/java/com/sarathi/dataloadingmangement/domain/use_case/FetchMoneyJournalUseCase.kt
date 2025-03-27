package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_MONEY_JOURNAL_DETAILS
import com.sarathi.dataloadingmangement.repository.MoneyJournalNetworkRepository
import javax.inject.Inject


class FetchMoneyJournalUseCase @Inject constructor(private val moneyJournalNetworkRepository: MoneyJournalNetworkRepository) :
    BaseApiCallNetworkUseCase() {

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
            val apiResponse = moneyJournalNetworkRepository.getMoneyJournalFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    moneyJournalNetworkRepository.saveMoneyJournalIntoDb(it)

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
        return SUBPATH_GET_MONEY_JOURNAL_DETAILS
    }
}