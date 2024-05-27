package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.TASK_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse


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
    var status: String? = "",
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var subjectId: Int,
    var localTaskId: String,
    var startOffset: Int,
    var endOffset: Int,
    var isActive: Int = 1,
) {
    companion object {
        fun getActivityTaskEntity(
            userId: String,
            missionId: Int,
            activityId: Int,
            task: TaskResponse
        ): ActivityTaskEntity {
            return ActivityTaskEntity(
                userId = userId,
                missionId = missionId,
                activityId = activityId,
                taskId = task.id ?: 0,
                status = task.taskStatus,
                subjectId = task.subjectId ?: -1,
                startOffset = task.startOffset,
                endOffset = task.endOffset,
                localTaskId = ""
            )
        }

    }
}
