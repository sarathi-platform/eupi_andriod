package com.patsurvey.nudge.activities.settings.domain.use_case

import android.net.Uri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.SummaryFileDto
import com.patsurvey.nudge.activities.settings.domain.repository.GetSummaryFileRepository
import javax.inject.Inject

class GetSummaryFileUseCase @Inject constructor(private val getSummaryFileRepository: GetSummaryFileRepository) {

    suspend operator fun invoke(
        userId: String,
        mobileNo: String,
        fileNameWithoutExtension: String,
        fileNameWithExtension: String
    ): Pair<String, Uri?>? {

        val content = ArrayList<SummaryFileDto>()
        val activities = getSummaryFileRepository.getActivitiesForUser(userId)

        activities.forEach {
            val tasksForActivity =
                getSummaryFileRepository.getTaskSummaryByStatus(userId, it.missionId, it.activityId)

            content.add(
                SummaryFileDto(
                    it.activityName.replace("Conduct ", BLANK_STRING),
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