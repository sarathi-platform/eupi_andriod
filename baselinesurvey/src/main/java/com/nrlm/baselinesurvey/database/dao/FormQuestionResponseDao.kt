package com.nrlm.baselinesurvey.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.FORM_QUESTION_RESPONSE_TABLE
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity

@Dao
interface FormQuestionResponseDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFormResponse(formQuestionResponseEntity: FormQuestionResponseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFormResponseList(formQuestionResponses: List<FormQuestionResponseEntity>)

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId and didiId = :didiId")
    fun getFormResponsesForQuestion(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where  userId=:userId and  surveyId=:surveyId AND sectionId=:sectionId AND didiId = :didiId")
    fun getFormResponsesForQuestionLive(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where   userId=:userId and surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun getFormResponsesForQuestionOption(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT COUNT(*) from $FORM_QUESTION_RESPONSE_TABLE where  userId=:userId and surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun isQuestionOptionAlreadyAnswered(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): Int

    @Query("Update $FORM_QUESTION_RESPONSE_TABLE set selectedValue = :selectedValue where  userId=:userId and  didiId = :didiId AND surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId AND referenceId = :referenceId")
    fun updateOptionItemValue(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int
    )


    @Query("SELECT COUNT(*) from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and  surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun getOptionItem(
        userId: String, surveyId: Int,
                      sectionId: Int,
                      questionId: Int,
                      optionId: Int,
                      referenceId: String,
                      didiId: Int): Int

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and referenceId = :referenceId")
    fun getFormResponseForReferenceId(
        userId: String,
        referenceId: String
    ): List<FormQuestionResponseEntity>

    @Query("DELETE from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and referenceId = :referenceId")
    fun deleteFormResponseQuestionForReferenceId(userId: String, referenceId: String)

    @Query("DELETE from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and optionId = :optionId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId AND didiId = :surveyeeId")
    fun deleteFormResponseQuestionForOption(
        userId: String,
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        surveyeeId: Int
    )

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId and surveyId = :surveyId and sectionId = :sectionId and didiId = :didiId")
    fun getFormQuestionCountForSection(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("Delete from $FORM_QUESTION_RESPONSE_TABLE where userId=:userId ")
    fun deleteAllFormQuestions(userId: String)


}