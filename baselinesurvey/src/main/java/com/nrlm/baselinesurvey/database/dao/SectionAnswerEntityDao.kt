package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.ANSWER_TABLE
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity

@Dao
interface SectionAnswerEntityDao {

    @Query("SELECT * FROM $ANSWER_TABLE where userId =:userId")
    fun getAllAnswer(userId: String): List<SectionAnswerEntity>

    @Query("SELECT * FROM $ANSWER_TABLE where userId=:userId and didiId = :didiId")
    fun getAllAnswerForDidi(userId: String, didiId: Int): List<SectionAnswerEntity>

    @Query("Select * FROM $ANSWER_TABLE where userId=:userId and  didiId = :didiId and sectionId = :sectionId")
    fun getSectionAnswerForDidi(
        userId: String,
        sectionId: Int,
        didiId: Int
    ): List<SectionAnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(answer: SectionAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(answers: List<SectionAnswerEntity>)

    @Query("Update $ANSWER_TABLE set optionItems = :optionItems, questionType=:questionType, questionSummary=:questionSummary where userId=:userId and didiId = :didiId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId")
    fun updateAnswer(
        userId: String,
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    )

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where userId=:userId and didiId = :didiId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId")
    fun isQuestionAlreadyAnswered(
        userId: String,
        didiId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int
    ): Int

    @Query("Delete from $ANSWER_TABLE where userId=:userId ")
    fun deleteAllSectionAnswer(userId: String)

    @Query("Delete from $ANSWER_TABLE where didiId = :didiId and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun deleteAnswerForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    )

}