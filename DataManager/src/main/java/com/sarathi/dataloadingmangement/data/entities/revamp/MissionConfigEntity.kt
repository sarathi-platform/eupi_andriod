package com.sarathi.dataloadingmangement.data.entities.revamp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MISSION_CONFIG_TABLE_NAME

@Entity(tableName = MISSION_CONFIG_TABLE_NAME)
data class MissionConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var missionName: String,
    var missionId: Int,
    var missionType: String
) {
    companion object {

        fun getMissionConfigEntity(
            missionId: Int,
            missionType: String,
            missionName: String,
            userId: String,
        ): MissionConfigEntity {
            return MissionConfigEntity(
                id = 0,
                missionId = missionId,
                missionType = missionType,
                missionName = missionName,
                userId = userId
            )
        }
    }
}
