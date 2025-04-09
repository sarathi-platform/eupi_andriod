package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ApiStatus
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupListFromDbRepository
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository,
    private val fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository,
    apiCallJournalRepository: IApiCallJournalRepository,
    private val coreSharedPrefs: CoreSharedPrefs
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {

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
                    customData = customData,
                )
            ) {
                return false
            }
            // Fetch small group list from the database once
            val smallGroupList =
                fetchSmallGroupListFromDbRepository.getSmallGroupListForUser(coreSharedPrefs.getUniqueUserIdentifier())
            smallGroupList.forEach { smallGroup ->
                val requestPayload = mapOf("smallGroupId" to smallGroup.smallGroupId)
                fetchSmallGroupAttendanceHistoryFromNetworkRepository.fetchSmallGroupAttendanceHistoryFromNetwork(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                    smallGroupId = smallGroup.smallGroupId
                )
            }
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.SUCCESS.name,
                customData = customData,
                errorMsg = BLANK_STRING
            )
        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = apiException.stackTraceToString()
            )
            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
        return false
    }

    suspend fun isFetchSmallGroupAttendanceHistoryFromNetworkAPIFailed(): Boolean {
        return fetchSmallGroupAttendanceHistoryFromNetworkRepository.isFetchSmallGroupAttendanceHistoryFromNetworkAPIStatus()?.status == DEFAULT_ERROR_CODE
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
    }

}