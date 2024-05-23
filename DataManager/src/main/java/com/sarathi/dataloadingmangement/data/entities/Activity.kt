package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.MissionActivityModel
import com.sarathi.dataloadingmangement.util.ACTIVITY_TABLE_NAME
import com.sarathi.dataloadingmangement.util.BLANK_STRING

@Entity(tableName = ACTIVITY_TABLE_NAME)
data class Activity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var activityId: Int,
    var activityName: String,
    var activityType: String,
    var activityTypeId: Int,
    var doer: String,
    var endDate: String,
    var reviewer: String,
    var startDate: String,
    var actualStartDate: String = BLANK_STRING,
    val actualCompleteDate: String = BLANK_STRING,
    var subject: String,
    var status: String?,
    var activityTaskSize: Int,
    var activityStatus: Int,
    var pendingDidi: Int,
    val isAllTask: Boolean,
    var language: String?,
    var isActive: Int
) {
    companion object {
        fun getMissionActivityEntity(
            userId: String,
            missionId: Int,
            activityTaskSize: Int,
            activity: MissionActivityModel
        ): Activity {
            return Activity(
                userId = userId,
                missionId = missionId,
                activityId = activity.activityId,
                activityTypeId = activity.activityTypeId,
                activityName = activity.activityName,
                activityType = activity.activityType,
                doer = activity.doer,
                startDate = activity.startDate,
                endDate = activity.endDate,
                reviewer = activity.reviewer,
                subject = activity.subject,
                status = "",
                activityTaskSize = activityTaskSize,
                activityStatus = 0,
                pendingDidi = activityTaskSize,
                isAllTask = false,
                language = activity.language,
                isActive = 1
            )
        }

    }
}
