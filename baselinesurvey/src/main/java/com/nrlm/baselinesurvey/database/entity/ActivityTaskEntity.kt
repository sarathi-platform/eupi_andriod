package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_CODE
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.model.datamodel.MissionTaskModel
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.toStringList

@Entity(tableName = TASK_TABLE_NAME)
data class ActivityTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: Int,
    var missionId: Int,
    var activityId: Int,
    var taskId: Int,
    var didiId: Int,
    var taskDate: String,
    var taskName: String,
    var status: String? = SurveyState.NOT_STARTED.name,
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var activityName: String,
    var activityState: Int,
    var subjectId: Int,
    var language: String?,


    ) {
    companion object {
        fun getActivityTaskEntity(
            userId: Int,
            missionId: Int,
            activityId: Int,
            activityName: String,
            task: MissionTaskModel
        ): ActivityTaskEntity {
            return ActivityTaskEntity(
                userId = userId,
                missionId = missionId,
                activityId = activityId,
                taskId = task.id ?: 0,
                didiId = task.subjectId ?: -1,
                taskDate = task.taskDate,
                taskName = task.taskName,
                status = getStatusForTask(task.status ?: SurveyState.NOT_STARTED.name),
                activityName = activityName,
                activityState = 0,
                subjectId = task.subjectId ?: -1,
                language = task.language ?: DEFAULT_LANGUAGE_CODE
            )
        }

        fun getStatusForTask(status: String): String {
            return SurveyState.values().toStringList().find { it.equals(status, true) }
                ?: SurveyState.NOT_STARTED.name
        }
    }
}