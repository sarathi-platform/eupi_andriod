package com.sarathi.dataloadingmangement.data.dao.smallGroup

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
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

    @Query("SELECT * from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId and smallGroupId = :smallGroupId and date = :lastEntryDate")
    suspend fun getAllMappingForSmallGroup(
        userId: String,
        smallGroupId: Int,
        lastEntryDate: Long
    ): List<SmallGroupDidiMappingEntity>

    @Query("SELECT * from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId and date = :lastEntryDate")
    suspend fun getAllMappingForUser(
        userId: String,
        lastEntryDate: Long
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

        return try {
            val lastEntryDate = getLastEntryDateForUser(userId)
            getAllMappingForUserForUiByDate(userId, lastEntryDate)
        } catch (ex: Exception) {
            CoreLogger.e(
                CoreAppDetails.getContext()!!,
                "SmallGroupDidiMappingDao",
                "getAllMappingForUserByDate exception: ${ex.message}",
                ex,
                true
            )
            emptyList()
        }


    }

    @Transaction
    suspend fun getAllMappingForUserByDateAndSmallGroupId(
        userId: String,
        smallGroupId: Int
    ): SmallGroupSubTabUiModel {

        return try {
            val lastEntryDate = getLastEntryDateForUser(userId)
            getAllMappingForUserForUiByDateAndSmallGroupId(userId, smallGroupId, lastEntryDate)
        } catch (ex: Exception) {
            CoreLogger.e(
                CoreAppDetails.getContext()!!,
                "SmallGroupDidiMappingDao",
                "getAllMappingForUserByDateAndSmallGroupId exception: ${ex.message}",
                ex,
                true
            )
            SmallGroupSubTabUiModel.getEmptyModel()
        }

    }

    @Transaction
    suspend fun getAllLatestMappingForSmallGroup(
        userId: String,
        smallGroupId: Int
    ): List<SmallGroupDidiMappingEntity> {

        return try {
            val lastEntryDate = getLastEntryDateForUser(userId)
            getAllMappingForSmallGroup(
                userId = userId,
                smallGroupId = smallGroupId,
                lastEntryDate = lastEntryDate
            )
        } catch (ex: Exception) {
            CoreLogger.e(
                CoreAppDetails.getContext()!!,
                "SmallGroupDidiMappingDao",
                "getAllLatestMappingForSmallGroup exception: ${ex.message}",
                ex,
                true
            )
            emptyList()
        }

    }

    @Transaction
    suspend fun getAllLatestMappingForUser(
        userId: String
    ): List<SmallGroupDidiMappingEntity> {
        return try {
            val lastEntryDate = getLastEntryDateForUser(userId)
            getAllMappingForUser(
                userId = userId,
                lastEntryDate = lastEntryDate
            )
        } catch (ex: Exception) {
            CoreLogger.e(
                CoreAppDetails.getContext()!!,
                "SmallGroupDidiMappingDao",
                "getAllLatestMappingForSmallGroup exception: ${ex.message}",
                ex,
                true
            )
            emptyList()
        }

    }

    @Query("DELETE from $SMALL_GROUP_DIDI_MAPPING_TABLE where userId = :userId")
    fun deleteSmallGroupDidiMappingForUser(userId: String)

}