package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.ANSWER_TABLE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerUiModel
import com.sarathi.dataloadingmangement.repository.LanguageAttributeReferenceType


@Dao
interface SurveyAnswersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity)


    @Query("select * from ques_answer_table where userId =:userId and subjectId=:subjectId and sectionId=:sectionId and referenceId=:referenceId")
    fun getSurveyAnswers(
        userId: String,
        subjectId: Int,
        sectionId: Int,
        referenceId: String
    ): List<SurveyAnswerEntity>

    @Query("select count(*) from ques_answer_table where userId =:userId and subjectId=:subjectId and sectionId=:sectionId and questionId =:questionId and referenceId=:referenceId")
    fun getSurveyAnswers(
        userId: String,
        subjectId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String
    ): Int

    @Query(
        "SELECT \n" +
                "    ques_answer_table.subjectId,\n" +
                "    ques_answer_table.taskId,\n" +
                "    ques_answer_table.questionId,\n" +
                "    ques_answer_table.referenceId,\n" +
                "    ques_answer_table.sectionId,\n" +
                "    ques_answer_table.surveyId,\n" +
                "    GROUP_CONCAT(tag_reference_table.value, ',') AS tagId, \n" +
                "    ques_answer_table.optionItems,\n" +
                "    ques_answer_table.questionSummary,\n" +
                "    ques_answer_table.questionType,\n" +
                "    form_table.isFormGenerated\n" +
                "FROM \n" +
                "    ques_answer_table  \n" +
                "LEFT JOIN \n" +
                "    form_table ON ques_answer_table.referenceId = form_table.localReferenceId \n" +
                "LEFT JOIN \n" +
                "    tag_reference_table ON ques_answer_table.questionId = tag_reference_table.referenceId \n" +
                "                        AND tag_reference_table.userId = :userId \n" +
                "                        AND tag_reference_table.referenceType = :referenceType \n" +
                "WHERE \n" +
                "    ques_answer_table.userId = :userId \n" +
                "    AND ques_answer_table.taskId = :taskId \n" +
                "    AND ques_answer_table.sectionId = :sectionId \n" +
                "    AND ques_answer_table.surveyId = :surveyId \n" +
                "GROUP BY \n" +
                "    ques_answer_table.id;"
    )
    fun getSurveyAnswersForSummary(
        userId: String,
        taskId: Int,
        sectionId: Int,
        surveyId: Int,
        referenceType: String = LanguageAttributeReferenceType.QUESTION.name
    ): List<SurveyAnswerFormSummaryUiModel>

    @Query("Update $ANSWER_TABLE set optionItems = :optionItems,answerValue =:answerValue, questionType=:questionType, questionSummary=:questionSummary where userId=:userId and subjectId = :subjectId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId and referenceId=:referenceId")
    fun updateAnswer(
        userId: String,
        subjectId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionsUiModel>,
        questionType: String,
        questionSummary: String,
        answerValue: String,
        referenceId: String
    )

    @Transaction
    fun insertOrModifySurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity) {
        if (getSurveyAnswers(
                surveyAnswerEntity.userId ?: BLANK_STRING,
                surveyAnswerEntity.subjectId,
                surveyAnswerEntity.sectionId,
                surveyAnswerEntity.questionId,
                surveyAnswerEntity.referenceId
            ) == 0
        ) {
            insertSurveyAnswer(surveyAnswerEntity)
        } else {
            updateAnswer(
                userId = surveyAnswerEntity.userId ?: BLANK_STRING,
                surveyId = surveyAnswerEntity.surveyId,
                questionId = surveyAnswerEntity.questionId,
                questionType = surveyAnswerEntity.questionType,
                questionSummary = surveyAnswerEntity.questionSummary ?: BLANK_STRING,
                sectionId = surveyAnswerEntity.sectionId,
                subjectId = surveyAnswerEntity.subjectId,
                optionItems = surveyAnswerEntity.optionItems,
                answerValue = surveyAnswerEntity.answerValue,
                referenceId = surveyAnswerEntity.referenceId
            )
        }
    }

    @Query("Delete from ques_answer_table where userId =:userId and sectionId=:sectionId and taskId=:taskId and referenceId =:referenceId and surveyId=:surveyId")
    fun deleteSurveyAnswer(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int

    @Query(
        "select " +
                "ques_answer_table.taskId,\n" +
                "ques_answer_table.subjectId,\n" +
                "ques_answer_table.questionId,\n" +
                "ques_answer_table.referenceId,\n" +
                "ques_answer_table.sectionId,\n" +
                "ques_answer_table.surveyId,\n" +
                " group_concat(tag_reference_table.value,',') as tagId," +
                "ques_answer_table.optionItems,\n" +
                "ques_answer_table.questionSummary,\n" +
                "ques_answer_table.grantId,\n" +
                "ques_answer_table.grantType,\n" +
                "ques_answer_table.questionType\n" +
                " from ques_answer_table " +
                " left join tag_reference_table on ques_answer_table.questionId= tag_reference_table.referenceId " +
                "where ques_answer_table.userId =:uniqueUserIdentifier and ques_answer_table.subjectId=:subjectId and ques_answer_table.taskId=:taskId " +
                "and tag_reference_table.userId=:uniqueUserIdentifier and tag_reference_table.referenceType=:referenceType group by ques_answer_table.id"
    )
    fun getSurveyAnswerForTag(
        taskId: Int,
        subjectId: Int,
        uniqueUserIdentifier: String,
        referenceType: String = LanguageAttributeReferenceType.QUESTION.name

    ): List<SurveyAnswerUiModel>

    @Query(
        "select " +
                "ques_answer_table.taskId,\n" +
                "ques_answer_table.subjectId,\n" +
                "ques_answer_table.questionId,\n" +
                "ques_answer_table.referenceId,\n" +
                "ques_answer_table.sectionId,\n" +
                "ques_answer_table.surveyId,\n" +
                " group_concat(tag_reference_table.value,',') as tagId," +
                "ques_answer_table.optionItems,\n" +
                "ques_answer_table.questionSummary,\n" +
                "ques_answer_table.grantId,\n" +
                "ques_answer_table.grantType,\n" +
                "ques_answer_table.questionType\n" +
                " from ques_answer_table " +
                " left join tag_reference_table on ques_answer_table.questionId= tag_reference_table.referenceId " +
                "where ques_answer_table.userId =:uniqueUserIdentifier and ques_answer_table.subjectId=:subjectId and ques_answer_table.taskId=:taskId " +
                "and ques_answer_table.referenceId=:referenceId " +
                "and tag_reference_table.userId=:uniqueUserIdentifier and tag_reference_table.referenceType=:referenceType group by ques_answer_table.id"
    )
    fun getSurveyAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        referenceId: String,
        uniqueUserIdentifier: String,
        referenceType: String = LanguageAttributeReferenceType.QUESTION.name
    ): List<SurveyAnswerUiModel>

    @Query("select * from ques_answer_table where userId =:uniqueUserIdentifier and questionType=:questionType")
    fun getSurveyAnswerImageKeys(
        questionType: String,
        uniqueUserIdentifier: String
    ): List<SurveyAnswerEntity>?

    @Query("select * from ques_answer_table where userId =:uniqueUserIdentifier")
    fun getAllSurveyAnswerForUser(
        uniqueUserIdentifier: String
    ): List<SurveyAnswerEntity>

    @Query("Delete from ques_answer_table where userId =:userId ")
    fun deleteSurveyAnswerForUser(
        userId: String,
    ): Int

    @Query("SELECT DISTINCT referenceId from ques_answer_table where surveyId = :surveyId and sectionId = :sectionId and taskId = :taskId and grantId = :grantId and questionId in (:questionIds)")
    fun getTotalSavedFormResponsesCount(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        grantId: Int = 0,
        questionIds: List<Int>
    ): List<String>


    @Query("SELECT * from ques_answer_table where surveyId = :surveyId and sectionId = :sectionId and taskId = :taskId and grantId = :grantId and questionId in (:questionIds)")
    fun getSurveyAnswersForQuestionIds(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        grantId: Int = 0,
        questionIds: List<Int>
    ): List<SurveyAnswerEntity>
}