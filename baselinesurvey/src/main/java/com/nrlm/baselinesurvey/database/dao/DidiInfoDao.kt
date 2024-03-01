package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.DIDI_INFO_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity

@Dao
interface DidiInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(didiIntoEntity: DidiIntoEntity)

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where didiId=:didiId ")
    suspend fun getDidiInfo(didiId: Int): DidiIntoEntity

    @Query("SELECT * FROM $DIDI_INFO_TABLE_NAME where didiId=:didiId")
    fun getDidiInfoLive(didiId: Int): LiveData<List<DidiIntoEntity>>
}