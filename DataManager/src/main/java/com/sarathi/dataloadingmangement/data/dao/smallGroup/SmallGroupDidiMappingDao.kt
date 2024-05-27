package com.sarathi.dataloadingmangement.data.dao.smallGroup

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.util.SMALL_GROUP_DIDI_MAPPING_TABLE

@Dao
interface SmallGroupDidiMappingDao {

    @Insert
    suspend fun insertSmallGroupDidiMapping(smallGroupDidiMappingEntity: SmallGroupDidiMappingEntity)

    @Insert
    suspend fun insertAllSmallGroupDidiMapping(smallGroupDidiMappingEntityList: List<SmallGroupDidiMappingEntity>)

    @Query("SELECT * from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId")
    suspend fun getAllMappingForUser(userId: String): List<SmallGroupDidiMappingEntity>

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

}