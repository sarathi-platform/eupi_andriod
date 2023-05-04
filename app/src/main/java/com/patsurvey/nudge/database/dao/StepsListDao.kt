package com.patsurvey.nudge.database.dao

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

    @Query("SELECT * FROM $STEPS_LIST_TABLE")
    fun getAllSteps(): List<StepListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<StepListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: StepListEntity)

}