package com.sarathi.missionactivitytask.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_TABLE_NAME
import com.sarathi.missionactivitytask.constants.enums.MATStates
import com.sarathi.missionactivitytask.constants.enums.toStringList
import com.sarathi.missionactivitytask.models.response.MissionResponseModel

@Entity(tableName = MISSION_TABLE_NAME)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionId: Int,
    var missionName: String,
    var startDate: String,
    var endDate: String,
    var status: String,
    var activityTaskSize: Int,
    var missionStatus: Int,
    var pendingActivity: Int,
    var activityComplete: Int,
    var language: String?,
    var actualStartDate: String = BLANK_STRING,
    var actualCompletedDate: String = BLANK_STRING,
    var isActive: Int = 1
) {
    companion object {
        fun getMissionEntity(
            userId: String,
            activityTaskSize: Int,
            mission: MissionResponseModel
        ): MissionEntity {
            return MissionEntity(
                id = 0,
                userId = userId,
                missionId = mission.missionId,
                missionName = mission.missionName,
                startDate = mission.startDate,
                endDate = mission.endDate,
                status = getStatusForMission(mission.missionStatus ?: MATStates.NOT_STARTED.name),
                activityTaskSize = activityTaskSize,
                missionStatus = 0,
                pendingActivity = activityTaskSize,
                activityComplete = MATStates.NOT_STARTED.ordinal,
                language = mission.language
            )
        }

        private fun getStatusForMission(status: String): String {
            return MATStates.values().toStringList().find { it.equals(status, true) }
                ?: MATStates.NOT_STARTED.name
        }
    }
}