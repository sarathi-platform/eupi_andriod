package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nrlm.baselinesurvey.DIDI_INFO_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity

@Dao
interface DidiInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidiInfo(didiIntoEntity: DidiIntoEntity)

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where didiId=:didiId ")
    suspend fun getDidiInfo(didiId: Int): DidiIntoEntity

    @Update
    fun updateDidiInfo(didiIntoEntity: DidiIntoEntity)

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where didiId=:didiId")
    fun getDidiInfoLive(didiId: Int): LiveData<List<DidiIntoEntity>>

    @Query("delete from $DIDI_INFO_TABLE_NAME")
    fun deleteAllDidiInfo()

    @Query("SELECT COUNT(*) from $DIDI_INFO_TABLE_NAME where didiId = :didiId")
    fun isDidiInfoAvailable(didiId: Int): Int

    @Transaction
    fun checkAndUpdateDidiInfo(didiIntoEntity: DidiIntoEntity) {
        val isDidiInfoAvailable = isDidiInfoAvailable(didiIntoEntity.didiId ?: 0)
        if (isDidiInfoAvailable > 0) {
            updateDidiInfo(didiIntoEntity)
        } else {
            insertDidiInfo(didiIntoEntity)
        }
    }

}