package com.sarathi.dataloadingmangement.data.dao.revamp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.MISSION_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.revamp.MissionConfigEntity

@Dao
interface MissionConfigEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMissionConfig(missionConfigEntity: MissionConfigEntity)

    @Query("Delete from $MISSION_CONFIG_TABLE_NAME where  missionId=:missionId and userId=:uniqueUserIdentifier")
    fun deleteMissionConfig(
        missionId: Int,
        uniqueUserIdentifier: String
    )

    @Query("Delete from $MISSION_CONFIG_TABLE_NAME where userId=:uniqueUserIdentifier")
    fun deleteMissionConfigForUser(
        uniqueUserIdentifier: String
    )
}