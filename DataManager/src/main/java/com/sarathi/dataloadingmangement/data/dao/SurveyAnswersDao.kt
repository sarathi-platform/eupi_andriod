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
        "select ques_answer_table.subjectId\n" +
                ",ques_answer_table.taskId,\n" +
                "ques_answer_table.referenceId,\n" +
                "ques_answer_table.sectionId,\n" +
                "ques_answer_table.surveyId,\n" +
                "ques_answer_table.tagId,\n" +
                "ques_answer_table.optionItems,\n" +
                "ques_answer_table.questionSummary,\n" +
                "ques_answer_table.questionType,\n" +
                "form_table.isFormGenerated\n" +
                " from ques_answer_table " +
                "left join form_table on ques_answer_table.referenceId =form_table.localReferenceId  where ques_answer_table.userId =:userId and ques_answer_table.taskId=:taskId and ques_answer_table.sectionId=:sectionId and ques_answer_table.surveyId=:surveyId"
    )
    fun getSurveyAnswersForSummary(
        userId: String,
        taskId: Int,
        sectionId: Int,
        surveyId: Int
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

    @Query("select * from ques_answer_table where userId =:uniqueUserIdentifier and subjectId=:subjectId and taskId=:taskId and tagId =:tagId")
    fun getSurveyAnswerForTag(
        taskId: Int,
        subjectId: Int,
        tagId: Int,
        uniqueUserIdentifier: String
    ): List<SurveyAnswerEntity>

    @Query("select * from ques_answer_table where userId =:uniqueUserIdentifier and subjectId=:subjectId and taskId=:taskId and tagId =:tagId and referenceId=:referenceId")
    fun getSurveyAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: Int,
        referenceId: String,
        uniqueUserIdentifier: String
    ): SurveyAnswerEntity

    @Query("select * from ques_answer_table where userId =:uniqueUserIdentifier and questionType=:questionType")
    fun getSurveyAnswerImageKeys(
        questionType: String,
        uniqueUserIdentifier: String
    ): List<SurveyAnswerEntity>?

}