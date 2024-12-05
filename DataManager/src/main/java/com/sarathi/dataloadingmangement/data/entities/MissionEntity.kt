package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MISSION_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse


@Entity(tableName = MISSION_TABLE_NAME)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var programmeId: Int,
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
    var isDataLoaded: Int = 0,
    var isActive: Int = 1,
    var missionOrder: Int = 1
) {
    companion object {
        fun getMissionEntity(
            userId: String,
            activityTaskSize: Int,
            programmeId: Int,
            mission: MissionResponse,
        ): MissionEntity {

            return MissionEntity(
                id = 0,
                userId = userId,
                missionId = mission.id,
                startDate = mission.actualStartDate ?: BLANK_STRING,
                endDate = mission.actualEndDate ?: BLANK_STRING,
                status = mission.missionStatus,
                activityTaskSize = activityTaskSize,
                missionStatus = mission.missionStatus,
                pendingActivity = activityTaskSize,
                activityComplete = 0,
                startOffset = mission.startOffset,
                endOffset = mission.endOffset,
                programmeId = programmeId,
                isDataLoaded = 0,
                missionOrder = mission.order
            )
        }

    }
}