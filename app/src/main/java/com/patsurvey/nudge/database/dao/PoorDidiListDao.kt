package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.utils.POOR_DIDI_TABLE

@Dao
interface PoorDidiListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoorDidi(poorDidi: PoorDidiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPoorDidiList(poorDidiList: List<PoorDidiEntity>)

    @Query("Select * from $POOR_DIDI_TABLE where villageId = :villageId")
    fun getAllPoorDidisForVillage(villageId: Int): List<PoorDidiEntity>

    @Query("Delete from $POOR_DIDI_TABLE")
    fun deleteAllDidis()

}