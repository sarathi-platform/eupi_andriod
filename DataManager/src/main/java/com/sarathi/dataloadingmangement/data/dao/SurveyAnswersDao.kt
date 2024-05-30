package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity


@Dao
interface SurveyAnswersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity)


}