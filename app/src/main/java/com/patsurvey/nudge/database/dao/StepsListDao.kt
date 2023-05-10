package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.LANGUAGE_TABLE_NAME
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE

@Dao
interface StepsListDao {

    @Query("SELECT * FROM $STEPS_LIST_TABLE ORDER BY orderNumber ASC")
    fun getAllSteps(): List<StepListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<StepListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: StepListEntity)


    @Query("UPDATE $STEPS_LIST_TABLE SET isComplete = true where id = :stepId")
    fun markStepAsComplete(stepId: Int)

    @Query("SELECT isComplete from $STEPS_LIST_TABLE where id = :id")
    fun isStepComplete(id: Int): Boolean

    @Query("SELECT isComplete from $STEPS_LIST_TABLE where id = :id")
    fun isStepCompleteLive(id: Int) : LiveData<Boolean>
}