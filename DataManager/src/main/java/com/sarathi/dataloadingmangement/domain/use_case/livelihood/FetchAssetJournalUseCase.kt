package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_ASSETS_JOURNAL_DETAILS
import com.sarathi.dataloadingmangement.repository.liveihood.AssetJournalNetworkRepositoryImpl
import javax.inject.Inject


class FetchAssetJournalUseCase @Inject constructor(private val assetJournalNetworkRepositoryImpl: AssetJournalNetworkRepositoryImpl) :
    BaseApiCallNetworkUseCase() {

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
                return false
            }
            val apiResponse = assetJournalNetworkRepositoryImpl.getAssetJournalFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    assetJournalNetworkRepositoryImpl.saveAssetJournalIntoDb(it)

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
        return SUBPATH_GET_ASSETS_JOURNAL_DETAILS
    }
}