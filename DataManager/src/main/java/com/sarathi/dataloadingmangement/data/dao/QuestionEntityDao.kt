package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.QUESTION_TABLE
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiEntity


@Dao
interface QuestionEntityDao {

    @Insert
    fun insertQuestion(questionEntity: QuestionEntity)

    @Query("Delete from $QUESTION_TABLE where userId=:userid and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId ")
    fun deleteSurveySectionQuestionFroLanguage(
        userid: String,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
    )

    @Query(
        "select \n" +
                " survey_language_attribute_table.description,\n" +
                " survey_language_attribute_table.paraphrase,\n" +
                " question_table.questionId,\n" +
                " question_table.originalValue,\n" +
                " question_table.sectionId,\n" +
                " question_table.surveyId,\n" +
                " question_table.formId,\n" +
                " question_table.questionImageUrl,\n" +
                " question_table.type,\n" +
                " question_table.gotoQuestionId,\n" +
                " question_table.`order` ,\n" +
                " question_table.isConditional,\n" +
                " question_table.isMandatory,\n" +
                " question_table.contentEntities,\n" +
                " survey_language_attribute_table.languageCode,\n" +
                " question_table.parentQuestionId,\n" +
                " group_concat(tag_reference_table.value,',') as tag" +
                "  from question_table inner join survey_language_attribute_table on question_table.questionId = survey_language_attribute_table.referenceId" +
                "  left join tag_reference_table on question_table.questionId= tag_reference_table.referenceId " +
                " where survey_language_attribute_table.referenceType =:referenceType \n" +
                "and survey_language_attribute_table.languageCode=:languageId AND question_table.userId=:userId and question_table.sectionId = :sectionId and question_table.surveyId = :surveyId and question_table.userId=:userId and tag_reference_table.userId=:userId and tag_reference_table.referenceType=:referenceType group by question_table.questionId Order by question_table.`order` asc "
    )
    fun getSurveySectionQuestionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: String,
        referenceType: String
    ): List<QuestionUiEntity>

    @Query("Select * from $QUESTION_TABLE where userId=:userid and surveyId = :surveyId and languageId = :languageId")
    fun getAllQuestionsForLanguage(
        userid: String,
        surveyId: Int,
        languageId: String
    ): List<QuestionEntity>

    @Query("SELECT * from $QUESTION_TABLE where userId=:userid and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId ")
    fun getQuestionForSurveySectionForLanguage(
        userid: String,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
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
    fun deleteAllQuestionsForUser(userid: String)

    @Query("SELECT `order` from $QUESTION_TABLE where  userId=:userid and surveyId = :surveyId and sectionId = :sectionId and questionId = :questionId")
    fun getOrderId(
        userid: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): Int?


}