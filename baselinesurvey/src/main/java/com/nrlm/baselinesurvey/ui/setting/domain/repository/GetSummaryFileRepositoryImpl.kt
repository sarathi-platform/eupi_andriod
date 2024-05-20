package com.nrlm.baselinesurvey.ui.setting.domain.repository

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.model.datamodel.SummaryFileDto
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.BLANK_STRING
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.utils.CoreLogger
import java.io.File
import java.io.FileWriter

class GetSummaryFileRepositoryImpl(
    private val activityTaskDao: ActivityTaskDao,
    private val missionActivityDao: MissionActivityDao
) : GetSummaryFileRepository {

    private val TAG = GetSummaryFileRepositoryImpl::class.java.simpleName

    override fun getTaskSummaryByStatus(
        userId: String,
        missionId: Int,
        activityId: Int
    ): List<SummaryFileDto> {
        return activityTaskDao.getTaskSummaryByStatus(userId, missionId, activityId)
    }

    override suspend fun getActivitiesForUser(userId: String): List<MissionActivityEntity> {
        return missionActivityDao.getAllActivities(userId)
    }

    override fun deleteOldSummaryFile(
        uniqueUserIdentifier: String,
        mobileNo: String,
        fileNameWithExtension: String
    ) {

        val context = BaselineCore.getAppContext()

        val fileDirectory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "$SARATHI_DIRECTORY_NAME/$mobileNo"
        )
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }

        val summaryFile = File(fileDirectory, fileNameWithExtension)

        try {
            if (summaryFile.exists() && summaryFile.isFile) {
                if (summaryFile.delete()) {
                    CoreLogger.d(
                        context,
                        TAG,
                        "deleteOldFiles -> file Deleted :" + summaryFile.getPath()
                    );
                } else {
                    CoreLogger.d(
                        context,
                        TAG,
                        "deleteOldFiles -> file not Deleted :" + summaryFile.getPath()
                    );
                }
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "deleteOldFiles -> file: ${summaryFile.name}, exception: ${ex.message}",
                ex,
                true
            )
        }

    }

    override fun writeFileForTheSummaryData(
        uniqueUserIdentifier: String,
        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        content: List<SummaryFileDto>,
        insertBlankRowForEmptyEntry: Boolean
    ): Pair<String, Uri?>? {

        val context = BaselineCore.getAppContext()

        var summaryStream: FileWriter? = null
        try {
            val fileDirectory = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "$SARATHI_DIRECTORY_NAME/$mobileNo"
            )
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }
            val summaryFile = File(fileDirectory, fileNameWithExtension)

            summaryStream = FileWriter(summaryFile, true)

            content.forEach {
                if (insertBlankRowForEmptyEntry && it.summaryKey == BLANK_STRING && it.summaryCount == BLANK_STRING) {
                    summaryStream.appendLine(BLANK_STRING)
                    summaryStream.appendLine(BLANK_STRING)
                } else {
                    var data = it.summaryKey
                    if (it.summaryCount != BLANK_STRING)
                        data = data + ": " + it.summaryCount
                    summaryStream.appendLine(data)
                }
            }


            summaryStream.close()

            return Pair(fileNameWithoutExtension, summaryFile.toUri())
        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "writeEventInAppDirectory -> exception: ${ex.message}",
                ex,
                true
            )
            summaryStream?.close()
            return null
        }

    }


}