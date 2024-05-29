package com.sarathi.dataloadingmangement.data.dao.smallGroup

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.SMALL_GROUP_DIDI_MAPPING_TABLE
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel

@Dao
interface SmallGroupDidiMappingDao {

    @Insert
    suspend fun insertSmallGroupDidiMapping(smallGroupDidiMappingEntity: SmallGroupDidiMappingEntity)

    @Insert
    suspend fun insertAllSmallGroupDidiMapping(smallGroupDidiMappingEntityList: List<SmallGroupDidiMappingEntity>)

    @Query("SELECT * from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId")
    suspend fun getAllMappingForUser(userId: String): List<SmallGroupDidiMappingEntity>

    @Query("SELECT userId, smallGroupId, smallGroupName, count(*) as didiCount from small_group_didi_mapping_table where userId = :userId and status = 1 group by smallGroupId")
    suspend fun getAllMappingForUserIdForUi(userId: String): List<SmallGroupSubTabUiModel>

    @Query("SELECT * from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId and smallGroupId = :smallGroupId")
    suspend fun getAllMappingForSmallGroup(
        userId: String,
        smallGroupId: Int
    ): List<SmallGroupDidiMappingEntity>

    @Query("SELECT count(*) from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId and smallGroupId = :smallGroupId")
    suspend fun getDidiCountForSmallGroup(userId: String, smallGroupId: Int): Int

    @Query("SELECT count(*) from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId and smallGroupId = :smallGroupId and didiId = :didiId")
    suspend fun isDidiMappingPresent(userId: String, smallGroupId: Int, didiId: Int): Int

    @Query("UPDATE $SMALL_GROUP_DIDI_MAPPING_TABLE set status = 2 where userId = :userId and smallGroupId = :smallGroupId")
    suspend fun markAllDidiAsInactiveForSmallGroup(userId: String, smallGroupId: Int)

    @Query("SELECT date from small_group_didi_mapping_table where userId = :userId group by date order by date DESC LIMIT 1")
    suspend fun getLastEntryDateForUser(userId: String): Long

    @Query("SELECT userId, smallGroupId, smallGroupName, count(*) as didiCount from small_group_didi_mapping_table where userId = :userId and status = 1 and date = :date group by smallGroupId")
    suspend fun getAllMappingForUserForUiByDate(
        userId: String,
        date: Long
    ): List<SmallGroupSubTabUiModel>

    @Query("SELECT userId, smallGroupId, smallGroupName, count(*) as didiCount from small_group_didi_mapping_table where userId = :userId and smallGroupId = :smallGroupId and status = 1 and date = :date group by smallGroupId")
    suspend fun getAllMappingForUserForUiByDateAndSmallGroupId(
        userId: String,
        smallGroupId: Int,
        date: Long
    ): SmallGroupSubTabUiModel

    @Transaction
    suspend fun getAllMappingForUserByDate(userId: String): List<SmallGroupSubTabUiModel> {

        val lastEntryDate = getLastEntryDateForUser(userId)
        return getAllMappingForUserForUiByDate(userId, lastEntryDate)

    }

    @Transaction
    suspend fun getAllMappingForUserByDateAndSmallGroupId(
        userId: String,
        smallGroupId: Int
    ): SmallGroupSubTabUiModel {

        val lastEntryDate = getLastEntryDateForUser(userId)
        return getAllMappingForUserForUiByDateAndSmallGroupId(userId, smallGroupId, lastEntryDate)

    }


}