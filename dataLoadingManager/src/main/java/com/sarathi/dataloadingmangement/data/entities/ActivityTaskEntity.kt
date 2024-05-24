package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.dataModel.MissionTaskModel
import com.sarathi.dataloadingmangement.util.BLANK_STRING
import com.sarathi.dataloadingmangement.util.TASK_TABLE_NAME


@Entity(tableName = TASK_TABLE_NAME)
data class ActivityTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var activityId: Int,
    var taskId: Int,
    var didiId: Int,
    var taskDate: String,
    var taskName: String,
    var status: String? = "",
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var activityName: String,
    var activityState: Int,
    var subjectId: Int,
    var language: String?,
    var localTaskId: String,
    var isActive: Int = 1,
) {
    companion object {
        fun getActivityTaskEntity(
            userId: String,
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
                status = "",
                activityName = activityName,
                activityState = 0,
                subjectId = task.subjectId ?: -1,
                language = task.language,
                localTaskId = ""
            )
        }

    }
}
