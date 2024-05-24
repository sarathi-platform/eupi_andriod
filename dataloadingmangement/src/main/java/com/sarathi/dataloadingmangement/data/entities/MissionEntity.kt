package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.util.BLANK_STRING
import com.sarathi.dataloadingmangement.util.MISSION_TABLE_NAME

@Entity(tableName = MISSION_TABLE_NAME)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var startOffset: Int,
    var endOffset: Int,
    var startDate: String,
    var endDate: String,
    var status: String,
    var activityTaskSize: Int,
    var missionStatus: String,
    var pendingActivity: Int,
    var activityComplete: Int,
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var isActive: Int = 1
) {
    companion object {
        fun getMissionEntity(
            userId: String,
            activityTaskSize: Int,
            mission: MissionResponse,
        ): MissionEntity {

            return MissionEntity(
                id = 0,
                userId = userId,
                missionId = mission.id,
                startDate = mission.actualStartDate,
                endDate = mission.actualEndDate,
                status = mission.missionStatus,
                activityTaskSize = activityTaskSize,
                missionStatus = mission.missionStatus,
                pendingActivity = activityTaskSize,
                activityComplete = 0,
                startOffset = mission.startOffset,
                endOffset = mission.endOffset
            )
        }

    }
}