package com.patsurvey.nudge.activities.settings.domain.repository

import android.net.Uri
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.model.SummaryFileDto
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.FileType
import com.nudge.core.utils.FileUtils
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import javax.inject.Inject

class GetSummaryFileRepositoryV2Impl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val taskDao: TaskDao,
    private val activityDao: ActivityDao,
    private val missionDao: MissionDao
) : GetSummaryFileRepositoryV2 {

    override fun getTaskSummaryByStatus(
        missionId: Int,
        activityId: Int
    ): List<SummaryFileDto> {
        return taskDao.getTaskSummaryByStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId
        )
    }

    override suspend fun getActivitiesForUser(missionId: Int): List<ActivityUiModel> {
        return activityDao.getActivities(
            coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = DEFAULT_LANGUAGE_CODE,
            missionId
        )
    }

    override suspend fun getMissionForUser(userId: String): List<MissionUiModel> {
        return missionDao.getMissions(userId, DEFAULT_LANGUAGE_CODE)

    }

    override fun deleteOldSummaryFile(
        mobileNo: String,
        fileNameWithExtension: String
    ) {
        val fileDirectory = FileUtils.getDirectory(mobileNo, FileType.DOCUMENTS)

        FileUtils.deleteFile(fileNameWithExtension, fileDirectory)
    }

    override fun writeFileForTheSummaryData(
        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        content: List<SummaryFileDto>,
        insertBlankRowForEmptyEntry: Boolean
    ): Pair<String, Uri?>? {
        val summaryFile = FileUtils.createFile(
            fileNameWithExtension,
            FileUtils.getDirectory(mobileNo, FileType.DOCUMENTS)
        )

        return FileUtils.writeToFile(
            fileNameWithoutExtension,
            summaryFile,
            content,
            insertBlankRowForEmptyEntry
        )
    }


}