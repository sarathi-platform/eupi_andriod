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

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId and didiId = :didiId")
    fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND didiId = :didiId")
    fun getFormResponsesForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId and didiId = :didiId")
    fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT COUNT(*) from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun isQuestionOptionAlreadyAnswered(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): Int

    @Query("Update $FORM_QUESTION_RESPONSE_TABLE set selectedValue = :selectedValue where didiId = :didiId AND surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId AND referenceId = :referenceId")
    fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int
    )


    @Query("SELECT COUNT(*) from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND referenceId = :referenceId AND didiId = :didiId and optionId = :optionId")
    fun getOptionItem(surveyId: Int,
                      sectionId: Int,
                      questionId: Int,
                      optionId: Int,
                      referenceId: String,
                      didiId: Int): Int

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where referenceId = :referenceId")
    fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity>

    @Query("DELETE from $FORM_QUESTION_RESPONSE_TABLE where referenceId = :referenceId")
    fun deleteFormResponseQuestionForReferenceId(referenceId: String)

    @Query("DELETE from $FORM_QUESTION_RESPONSE_TABLE where optionId = :optionId AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId AND didiId = :surveyeeId")
    fun deleteFormResponseQuestionForOption(
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        surveyeeId: Int
    )

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId = :surveyId and sectionId = :sectionId and didiId = :didiId")
    fun getFormQuestionCountForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("Delete from $FORM_QUESTION_RESPONSE_TABLE")
    fun deleteAllFormQuestions()


}