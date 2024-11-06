package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.BLANK_STRING
import com.nudge.core.toSafeInt
import com.sarathi.dataloadingmangement.SECTION_STATUS_TABLE_NAME
import com.sarathi.dataloadingmangement.model.survey.response.SectionStatusResponseModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum

@Entity(tableName = SECTION_STATUS_TABLE_NAME)
data class SectionStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "userId")
    var userId: String? = BLANK_STRING,

    @ColumnInfo(name = "missionId")
    val missionId: Int,

    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @ColumnInfo(name = "sectionId")
    val sectionId: Int,

    @ColumnInfo(name = "taskId")
    val taskId: Int,

    @ColumnInfo(name = "sectionStatus")
    val sectionStatus: String?,
) {
    companion object {

        fun geSectionStatusEntity(
            sectionStatus: SectionStatusResponseModel,
            userId: String,
            missionId: Int,
            taskId: Int
        ): SectionStatusEntity {
            return SectionStatusEntity(
                id = 0,
                userId = userId,
                missionId = missionId,
                surveyId = sectionStatus.surveyId ?: 0,
                taskId = taskId,
                sectionStatus = sectionStatus.status ?: SurveyStatusEnum.NOT_STARTED.name,
                sectionId = sectionStatus.sectionId.toSafeInt("-1")

            )
        }

    }
}

