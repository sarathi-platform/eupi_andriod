package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository,
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
            val smallGroupId = customData["smallGroupId"] as Int
            return fetchSmallGroupAttendanceHistoryFromNetworkRepository.fetchSmallGroupAttendanceHistoryFromNetwork(
                screenName = screenName,
                triggerType = triggerType,
                moduleName = moduleName,
                customData = customData,
                smallGroupId = smallGroupId
            )
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun isFetchSmallGroupAttendanceHistoryFromNetworkAPIFailed(): Boolean {
        return fetchSmallGroupAttendanceHistoryFromNetworkRepository.isFetchSmallGroupAttendanceHistoryFromNetworkAPIStatus()?.status == DEFAULT_ERROR_CODE
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
    }

}