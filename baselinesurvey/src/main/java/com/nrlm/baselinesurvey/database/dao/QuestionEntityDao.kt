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

    @Query("Delete from $QUESTION_TABLE where userId=:userid and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionQuestionFroLanguage(
        userid: String,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    )

    @Query("Select * from $QUESTION_TABLE where userId=:userid and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionQuestionForLanguage(
        userid: String,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): List<QuestionEntity>

    @Query("Select * from $QUESTION_TABLE where userId=:userid and surveyId = :surveyId and languageId = :languageId")
    fun getAllQuestionsForLanguage(
        userid: String,
        surveyId: Int,
        languageId: Int
    ): List<QuestionEntity>

    @Query("SELECT * from $QUESTION_TABLE where userId=:userid and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getQuestionForSurveySectionForLanguage(
        userid: String,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): QuestionEntity?

    @Query("Select * from $QUESTION_TABLE where userId=:userid ")
    fun getQuestions(userid: String): List<QuestionEntity?>?

    @Query("SELECT * from $QUESTION_TABLE where userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId and languageId = :languageId")
    fun getFormQuestionForId(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        languageId: Int
    ): QuestionEntity?


    @Query("SELECT tag from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getQuestionTag(userid: String, surveyId: Int, sectionId: Int, questionId: Int): Int

    @Query("Delete from $QUESTION_TABLE where userId=:userid ")
    fun deleteAllQuestions(userid: String)
}