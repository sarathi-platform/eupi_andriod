package com.sarathi.dataloadingmangement.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse

@Entity(tableName = ACTIVITY_TABLE_NAME)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var activityId: Int,
    var startOffset: Int,
    var endOffset: Int,
    var actualStartDate: String = BLANK_STRING,
    val actualEndDate: String = BLANK_STRING,
    var status: String?,
    var activityTaskSize: Int,
    var activityStatus: String,
    var taskSize: Int,
    val isAllTask: Boolean,
    var isActive: Int
) {
    companion object {
        fun getMissionActivityEntity(
            userId: String,
            missionId: Int,
            activityTaskSize: Int,
            activity: ActivityResponse,
        ): ActivityEntity {
            return ActivityEntity(
                userId = userId,
                missionId = missionId,
                activityId = activity.id,
                actualStartDate = activity.actualStartDate ?: BLANK_STRING,
                actualEndDate = activity.actualEndDate ?: BLANK_STRING,
                status = activity.activityStatus,
                activityTaskSize = activityTaskSize,
                activityStatus = activity.activityStatus,
                taskSize = activityTaskSize,
                isAllTask = false,
                isActive = 1,
                startOffset = activity.startOffset ?: DEFAULT_ID,
                endOffset = activity.endOffset ?: DEFAULT_ID
            )
        }

    }
}
