package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

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

}