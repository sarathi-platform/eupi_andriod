package com.patsurvey.nudge.activities.settings.domain.repository

import android.net.Uri
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nudge.core.model.SummaryFileDto

interface GetSummaryFileRepository {

    fun getTaskSummaryByStatus(
        userId: String,
        missionId: Int,
        activityId: Int
    ): List<SummaryFileDto>

    suspend fun getActivitiesForUser(userId: String): List<MissionActivityEntity>

    fun deleteOldSummaryFile(
        uniqueUserIdentifier: String,
        mobileNo: String,
        fileNameWithExtension: String
    )

    fun writeFileForTheSummaryData(
        uniqueUserIdentifier: String,
        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        content: List<SummaryFileDto>,
        insertBlankRowForEmptyEntry: Boolean = true
    ): Pair<String, Uri?>?

}