package com.patsurvey.nudge.activities.settings.domain.repository

import android.net.Uri
import com.nudge.core.model.SummaryFileDto
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel

interface GetSummaryFileRepositoryV2 {

    fun getTaskSummaryByStatus(
        missionId: Int,
        activityId: Int
    ): List<SummaryFileDto>

    suspend fun getActivitiesForUser(missionId: Int): List<ActivityUiModel>

    suspend fun getMissionForUser(userId: String): List<MissionUiModel>

    fun deleteOldSummaryFile(
        mobileNo: String,
        fileNameWithExtension: String
    )

    fun writeFileForTheSummaryData(

        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        content: List<SummaryFileDto>,
        insertBlankRowForEmptyEntry: Boolean = true
    ): Pair<String, Uri?>?

}