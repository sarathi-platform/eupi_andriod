package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.patsurvey.nudge.database.BpcScorePercentageEntity
import com.patsurvey.nudge.utils.BPC_SCORE_PERCENTAGE_TABLE

@Dao
interface BpcScorePercentageDao {

    @Insert
    fun insert(bpcScorePercentageEntity: BpcScorePercentageEntity)

    @Query("Select * from $BPC_SCORE_PERCENTAGE_TABLE where stateId = :stateId")
    fun getBpcScorePercentageForState(stateId: Int): BpcScorePercentageEntity

}