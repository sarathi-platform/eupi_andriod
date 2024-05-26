package com.sarathi.dataloadingmangement.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.util.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.util.BLANK_STRING

@Entity(tableName = ACTIVITY_TABLE_NAME)
data class MissionActivityEntity(
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
        ): MissionActivityEntity {
            return MissionActivityEntity(
                userId = userId,
                missionId = missionId,
                activityId = activity.id,
                actualStartDate = activity.actualStartDate,
                actualEndDate = activity.actualEndDate,
                status = activity.activityStatus,
                activityTaskSize = activityTaskSize,
                activityStatus = activity.activityStatus,
                taskSize = activityTaskSize,
                isAllTask = false,
                isActive = 1,
                startOffset = activity.startOffset,
                endOffset = activity.endOffset
            )
        }

    }
}
