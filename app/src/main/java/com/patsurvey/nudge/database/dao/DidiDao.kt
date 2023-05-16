package com.patsurvey.nudge.database.dao

import androidx.room.*
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.DIDI_TABLE

@Dao
interface DidiDao {

    @Query("SELECT * FROM $DIDI_TABLE ORDER BY id DESC")
    fun getAllDidis(): List<DidiEntity>

    @Query("SELECT * FROM $DIDI_TABLE where villageId = :villageId")
    fun getAllDidisForVillage(villageId: Int): List<DidiEntity>

    @Query("Select * FROM $DIDI_TABLE where id = :id")
    fun getDidi(id: Int): DidiEntity

    @Query("Select COUNT(*) FROM $DIDI_TABLE where name = :name AND address=:address AND guardianName=:guardianName AND cohortId=:tolaId AND villageId= :villageId")
    fun getDidiExist(name:String,address:String,guardianName:String,tolaId:Int,villageId:Int):Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidi(didi: DidiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(didis: List<DidiEntity>)

    @Update
    fun updateDidi(didi: DidiEntity)

    @Query("DELETE from $DIDI_TABLE")
    fun deleteDidiTable()

    @Query("UPDATE $DIDI_TABLE SET needsToPost = :needsToPost WHERE id in (:ids)")
    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean)

}