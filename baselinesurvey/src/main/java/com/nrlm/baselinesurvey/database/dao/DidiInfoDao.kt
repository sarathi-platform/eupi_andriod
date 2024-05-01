package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DIDI_INFO_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity

@Dao
interface DidiInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidiInfo(didiInfoEntity: DidiInfoEntity)

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where userId=:userId and didiId=:didiId ")
    suspend fun getDidiInfo(userId: String, didiId: Int): DidiInfoEntity?

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where userId=:userId")
    suspend fun getAllDidi(userId: String): List<DidiInfoEntity>

    @Update
    fun updateDidiInfo(didiInfoEntity: DidiInfoEntity)

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where  userId=:userId and didiId=:didiId")
    fun getDidiInfoLive(didiId: Int, userId: String): LiveData<List<DidiInfoEntity>>

    @Query("delete from $DIDI_INFO_TABLE_NAME where userId=:userId")
    fun deleteAllDidiInfo(userId: String)

    @Query("SELECT COUNT(*) from $DIDI_INFO_TABLE_NAME where userId=:userId and didiId = :didiId")
    fun isDidiInfoAvailable(userId: String, didiId: Int): Int

    @Transaction
    fun checkAndUpdateDidiInfo(didiInfoEntity: DidiInfoEntity,userId: String) {
        val isDidiInfoAvailable = isDidiInfoAvailable(
            userId = userId,
            didiInfoEntity.didiId ?: 0
        )
        didiInfoEntity.userId=userId?: BLANK_STRING
        if (isDidiInfoAvailable > 0) {
            updateDidiInfo(didiInfoEntity)
        } else {
            insertDidiInfo(didiInfoEntity)
        }
    }

}