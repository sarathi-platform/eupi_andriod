package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.database.entities.ApiStatusEntity
import com.sarathi.dataloadingmangement.network.response.AttendanceHistoryResponse
import com.sarathi.dataloadingmangement.network.response.DidiAttendanceDetail

interface FetchSmallGroupAttendanceHistoryFromNetworkRepository {

    suspend fun fetchSmallGroupAttendanceHistoryFromNetwork(smallGroupId: Int)

    suspend fun saveSmallGroupAttendanceHistoryToDb(attendanceHistoryResponse: AttendanceHistoryResponse)

    suspend fun saveAttendanceToAttributeTable(
        subjectDetails: DidiAttendanceDetail,
        date: Long
    ): Long

    suspend fun saveAttendanceToAttributeReferenceTable(
        subjectDetails: DidiAttendanceDetail,
        date: Long,
        refId: Long
    )

    suspend fun checkIfHistoryAvailable(subjectId: Int, date: Long): Int
    fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    )

    suspend fun isFetchSmallGroupAttendanceHistoryFromNetworkAPIStatus(): ApiStatusEntity?
}