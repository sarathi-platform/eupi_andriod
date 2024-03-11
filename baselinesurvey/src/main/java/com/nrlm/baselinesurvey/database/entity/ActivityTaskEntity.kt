package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.TASK_TABLE_NAME
import com.nrlm.baselinesurvey.model.datamodel.MissionTaskModel

@Entity(tableName = TASK_TABLE_NAME)
data class ActivityTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var missionId: Int,
    var activityId: Int,
    var taskId: Int,
    var didiId: Int,
    var taskDate: String,
    var taskName: String,
    var status: String,
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var activityName: String,
    var activityState: Int,
    var subjectId: Int,
    var language: String,


) {
    companion object {
        fun getActivityTaskEntity(
            missionId: Int,
            activityId: Int,
            activityName: String,
            task: MissionTaskModel
        ): ActivityTaskEntity {
            return ActivityTaskEntity(
                missionId = missionId,
                activityId = activityId,
                taskId = task.taskId,
                didiId = task.subjectId ?: -1,
                taskDate = task.taskDate,
                taskName = task.taskName,
                status = task.status,
                activityName = activityName,
                activityState = 0,
                subjectId = task.subjectId ?: -1,
                language = task.language
            )
        }
    }
}