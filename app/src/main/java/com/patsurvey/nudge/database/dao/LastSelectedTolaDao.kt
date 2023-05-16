package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.LastTolaSelectedEntity
import com.patsurvey.nudge.utils.LAST_SELECTED_TOLA_TABLE

@Dao
interface LastSelectedTolaDao {

    @Query("SELECT * FROM $LAST_SELECTED_TOLA_TABLE where villageId = :villageId")
    fun getTolaForVillage(villageId:Int): LastTolaSelectedEntity

    @Query("Select * FROM $LAST_SELECTED_TOLA_TABLE where tolaId = :tolaId")
    fun getSelectedTola(tolaId: Int): LastTolaSelectedEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSelectedTola(caste: LastTolaSelectedEntity)

    @Query("UPDATE $LAST_SELECTED_TOLA_TABLE SET tolaId = :tolaId, tolaName = :tolaName WHERE villageId = :villageId")
    fun updateSelectedTola(tolaId: Int,tolaName:String,villageId:Int)

    @Query("SELECT COUNT(*) FROM $LAST_SELECTED_TOLA_TABLE where villageId = :villageId")
    fun getTolaCountForVillage(villageId:Int): Long
}