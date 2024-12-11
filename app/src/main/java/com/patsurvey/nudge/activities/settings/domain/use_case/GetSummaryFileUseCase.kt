package com.patsurvey.nudge.activities.settings.domain.use_case

import android.net.Uri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nudge.core.BASELINE_ACTIVITY_NAME_PREFIX
import com.nudge.core.model.SummaryFileDto
import com.patsurvey.nudge.activities.settings.domain.repository.GetSummaryFileRepository
import com.patsurvey.nudge.activities.settings.domain.repository.GetSummaryFileRepositoryV2
import javax.inject.Inject

class GetSummaryFileUseCase @Inject constructor(
    private val getSummaryFileRepository: GetSummaryFileRepository,
    private val getSummaryFileRepositoryV2: GetSummaryFileRepositoryV2
) {

    suspend operator fun invoke(
        userId: String,
        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String,
        isBaselineV2: Boolean = false
    ): Pair<String, Uri?>? {
        val content = ArrayList<SummaryFileDto>()
        return if (isBaselineV2) {
            writeFileForBaselineV2(
                userId,
                content,
                mobileNo,
                fileNameWithExtension,
                fileNameWithoutExtension
            )
        } else {
            writeFileForBaselineV1(
                userId,
                content,
                mobileNo,
                fileNameWithExtension,
                fileNameWithoutExtension
            )
        }

    }

    private suspend fun writeFileForBaselineV2(
        userId: String,
        content: ArrayList<SummaryFileDto>,
        mobileNo: String,
        fileNameWithExtension: String,
        fileNameWithoutExtension: String
    ): Pair<String, Uri?>? {
        val mission = getSummaryFileRepositoryV2.getBaselineMissionForUser(userId)
        mission?.missionId?.let {
            val activities = getSummaryFileRepositoryV2.getActivitiesForUser(it)

            activities.forEach { activity ->
                val taskForActivity =
                    getSummaryFileRepositoryV2.getTaskSummaryByStatus(it, activity.activityId)

                content.add(
                    SummaryFileDto(
                        activity.description.replace(BASELINE_ACTIVITY_NAME_PREFIX, BLANK_STRING),
                        BLANK_STRING
                    )
                )
                content.addAll(taskForActivity)
                content.add(SummaryFileDto(BLANK_STRING, BLANK_STRING))
            }

            getSummaryFileRepositoryV2.deleteOldSummaryFile(mobileNo, fileNameWithExtension)

            return getSummaryFileRepositoryV2.writeFileForTheSummaryData(
                mobileNo,
                fileNameWithoutExtension,
                fileNameWithExtension,
                content
            )
        } ?: return Pair(fileNameWithoutExtension, Uri.EMPTY)
    }

    private suspend fun writeFileForBaselineV1(
        userId: String,
        content: ArrayList<SummaryFileDto>,
        mobileNo: String,
        fileNameWithExtension: String,
        fileNameWithoutExtension: String
    ): Pair<String, Uri?>? {
        val activities = getSummaryFileRepository.getActivitiesForUser(userId)

        activities.forEach {
            val tasksForActivity =
                getSummaryFileRepository.getTaskSummaryByStatus(userId, it.missionId, it.activityId)

            content.add(
                SummaryFileDto(
                    it.activityName.replace(BASELINE_ACTIVITY_NAME_PREFIX, BLANK_STRING),
                    BLANK_STRING
                )
            )
            content.addAll(tasksForActivity)
            content.add(SummaryFileDto(BLANK_STRING, BLANK_STRING))

        }

        getSummaryFileRepository.deleteOldSummaryFile(userId, mobileNo, fileNameWithExtension)

        return getSummaryFileRepository.writeFileForTheSummaryData(
            userId,
            mobileNo,
            fileNameWithoutExtension,
            fileNameWithExtension,
            content
        )
    }

}