package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_ERROR_CODE
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository
) {

    suspend operator fun invoke(smallGroupId: Int) {
        fetchSmallGroupAttendanceHistoryFromNetworkRepository.fetchSmallGroupAttendanceHistoryFromNetwork(
            smallGroupId
        )
    }
    suspend fun isFetchSmallGroupAttendanceHistoryFromNetworkAPIFailed(): Boolean {
        return fetchSmallGroupAttendanceHistoryFromNetworkRepository.isFetchSmallGroupAttendanceHistoryFromNetworkAPIStatus()?.status == DEFAULT_ERROR_CODE
    }

}