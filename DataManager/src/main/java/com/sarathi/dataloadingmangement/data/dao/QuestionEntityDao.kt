package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.QUESTION_TABLE
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity


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
        languageId: String
    )

    @Query("Select * from $QUESTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionQuestionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: String
    ): List<QuestionEntity>

    @Query("Select * from $QUESTION_TABLE where userId=:userid and surveyId = :surveyId and languageId = :languageId")
    fun getAllQuestionsForLanguage(
        userid: String,
        surveyId: Int,
        languageId: String
    ): List<QuestionEntity>

    @Query("SELECT * from $QUESTION_TABLE where userId=:userid and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getQuestionForSurveySectionForLanguage(
        userid: String,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: String
    ): QuestionEntity?

    @Query("Select * from $QUESTION_TABLE where userId=:userid ")
    fun getQuestions(userid: String): List<QuestionEntity?>?

    @Query("SELECT * from $QUESTION_TABLE where userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId and languageId = :languageId")
    fun getFormQuestionForId(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        languageId: String
    ): QuestionEntity?


    @Query("SELECT tag from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getQuestionTag(userid: String, surveyId: Int, sectionId: Int, questionId: Int): Int

    @Query("SELECT questionDisplay from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getQuestionDisplayName(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): String

    @Query("SELECT * from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getQuestionEntity(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): QuestionEntity?

    @Query("SELECT * from $QUESTION_TABLE where type = :type")
    fun getQuestionForType(type: String): QuestionEntity

    @Query("SELECT * from $QUESTION_TABLE where type in (:type) and userId = :userid")
    fun getQuestionForTypeForSurveySection(type: List<String>, userid: String): List<QuestionEntity>

    @Query("Delete from $QUESTION_TABLE where userId=:userid ")
    fun deleteAllQuestions(userid: String)

    @Query("SELECT `order` from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getOrderId(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): Int?

    //    @Query("select question_table.questionId,question_table.sectionId,question_table.type,question_table.languageId, question_table.questionDisplay, question_table.questionSummary,option_table.optionId, option_table.optionType, option_table.optionValue, option_table.display,option_table.summary from question_table inner join option_table on question_table.questionId =option_table.questionId where question_table.surveyId=:surveyId  and question_table.sectionId =:sectionId and question_table.languageId=:languageId and option_table.languageId=:languageId")
//    @Query(
//        "select question_table.questionId,\n" +
//                " question_table.surveyId, question_table.sectionId,\n" +
//                " question_table.type,  question_table.languageId,\n" +
//                "  question_table.questionDisplay, question_table.questionSummary,\n" +
//                "  option_table.optionId, option_table.optionType, option_table.optionValue,\n" +
//                "   option_table.display,option_table.summary from question_table inner join option_table on question_table.questionId =option_table.questionId\n" +
//                "   where question_table.surveyId=:surveyId\n" +
//                "   and question_table. sectionId =sectionId\n" +
//                "       and question_table.languageId=:languageId\n" +
//                "         and option_table.languageId=:languageId"
//    )
//    fun getQuestionOption(languageId: String, sectionId: Int, surveyId: Int): List<QuestionUiModel>

}