package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.ANSWER_TABLE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity


@Dao
interface SurveyAnswersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity)


    @Query("select * from ques_answer_table where userId =:userId and subjectId=:subjectId and sectionId=:sectionId")
    fun getSurveyAnswers(userId: String, subjectId: Int, sectionId: Int): List<SurveyAnswerEntity>

    @Query("select count(*) from ques_answer_table where userId =:userId and subjectId=:subjectId and sectionId=:sectionId and questionId =:questionId")
    fun getSurveyAnswers(userId: String, subjectId: Int, sectionId: Int, questionId: Int): Int

    @Query("Update $ANSWER_TABLE set optionItems = :optionItems,answerValue =:answerValue, questionType=:questionType, questionSummary=:questionSummary where userId=:userId and subjectId = :subjectId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId")
    fun updateAnswer(
        userId: String,
        subjectId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String,
        answerValue: String
    )

    @Transaction
    fun insertOrModifySurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity) {
        if (getSurveyAnswers(
                surveyAnswerEntity.userId ?: BLANK_STRING,
                surveyAnswerEntity.subjectId,
                surveyAnswerEntity.sectionId,
                surveyAnswerEntity.questionId
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
                answerValue = surveyAnswerEntity.answerValue


            )


        }


    }

}