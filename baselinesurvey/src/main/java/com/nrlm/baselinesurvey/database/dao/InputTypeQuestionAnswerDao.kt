package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.INPUT_TYPE_QUESTION_ANSWER_TABLE
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity

@Dao
interface InputTypeQuestionAnswerDao {

    @Query("SELECT * from $INPUT_TYPE_QUESTION_ANSWER_TABLE where userId=:userId and surveyId = :surveyId AND sectionId = :sectionId AND questionId = :questionId AND didiId = :didiId")
    fun getInputTypeAnswersForQuestion(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<InputTypeQuestionAnswerEntity>

    @Query("SELECT * from $INPUT_TYPE_QUESTION_ANSWER_TABLE where userId=:userId")
    fun getAllInputTypeAnswersForQuestion(
        userId: String,
    ): List<InputTypeQuestionAnswerEntity>

    @Query("SELECT * from $INPUT_TYPE_QUESTION_ANSWER_TABLE where userId=:userId and surveyId = :surveyId AND sectionId = :sectionId AND didiId = :didiId")
    fun getInputTypeAnswersForQuestionForDidi(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<InputTypeQuestionAnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveInputTypeAnswersForQuestion(inputTypeQuestionAnswerEntity: InputTypeQuestionAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveInputTypeAnswersForQuestion(inputTypeQuestionAnswerEntity: List<InputTypeQuestionAnswerEntity>)

    @Query("UPDATE $INPUT_TYPE_QUESTION_ANSWER_TABLE set inputValue = :inputValue where " +
            "surveyId = :surveyId " +
            "AND sectionId = :sectionId " +
            "AND userId = :userId " +
            "AND questionId = :questionId " +
            "AND didiId = :didiId " +
            "AND optionId = :optionId ")
    fun updateInputTypeAnswersForQuestion(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        optionId: Int,
        inputValue: String
    )

    @Query("SELECT COUNT(*) from $INPUT_TYPE_QUESTION_ANSWER_TABLE where userId=:userId and surveyId = :surveyId AND sectionId = :sectionId AND questionId = :questionId AND didiId = :didiId AND optionId = :optionItemId")
    fun isQuestionAlreadyAnswered(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        optionItemId: Int
    ): Int

    @Query("Delete from $INPUT_TYPE_QUESTION_ANSWER_TABLE where  userId=:userId ")
    fun deleteAllInputTypeAnswers(userId: String)

    @Query("DELETE from $INPUT_TYPE_QUESTION_ANSWER_TABLE where userId=:userId and surveyId = :surveyId AND sectionId = :sectionId AND questionId = :questionId AND didiId = :didiId AND optionId = :optionId")
    fun deleteInputTypeQuestion(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int,
        optionId: Int
    )

}