package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.QUESTION_TABLE
import com.nrlm.baselinesurvey.database.entity.QuestionEntity

@Dao
interface QuestionEntityDao {

    @Insert
    fun insertQuestion(questionEntity: QuestionEntity)

    @Query("Delete from $QUESTION_TABLE where questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionQuestionFroLanguage(questionId: Int, sectionId: Int, surveyId: Int, languageId: Int)

    @Query("Select * from $QUESTION_TABLE where sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionQuestionForLanguage(sectionId: Int, surveyId: Int, languageId: Int): List<QuestionEntity>

    @Query("Select * from $QUESTION_TABLE where surveyId = :surveyId and languageId = :languageId")
    fun getAllQuestionsForLanguage(surveyId: Int, languageId: Int): List<QuestionEntity>

    @Query("SELECT * from $QUESTION_TABLE where questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getQuestionForSurveySectionForLanguage(
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): QuestionEntity?

    @Query("Select * from $QUESTION_TABLE")
    fun getQuestions(): List<QuestionEntity?>?

    @Query("SELECT * from $QUESTION_TABLE where surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId and languageId = :languageId")
    fun getFormQuestionForId(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        languageId: Int
    ): QuestionEntity?


    @Query("SELECT tag from $QUESTION_TABLE where surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getQuestionTag(surveyId: Int, sectionId: Int, questionId: Int): String

}