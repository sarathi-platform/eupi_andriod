package com.patsurvey.nudge.database.dao

import androidx.room.*
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE
import com.patsurvey.nudge.utils.TOLA_TABLE

@Dao
interface TolaDao {

    @Query("SELECT * FROM $TOLA_TABLE where status = 1")
    fun getAllTolas(): List<TolaEntity>

    @Query("SELECT * FROM $TOLA_TABLE where villageId = :villageId and status = 1 ORDER BY date_created DESC")
    fun getAllTolasForVillage(villageId: Int): List<TolaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tolas: List<TolaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tola: TolaEntity)

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun removeTola(id: Int)

    @Query("SELECT * from $TOLA_TABLE where id = :id")
    fun fetchSingleTola(id: Int): TolaEntity

    @Query("UPDATE $TOLA_TABLE SET needsToPost = :needsToPost WHERE id in (:ids)")
    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean)

    @Query("DELETE from $TOLA_TABLE")
    fun deleteTolaTable()

    @Query("UPDATE $TOLA_TABLE SET status = :status, needsToPost = true WHERE id = :id")
    fun deleteTolaOffline(id: Int, status: Int)
}