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

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId and didiId = :didiId")
    fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId and didiId = :didiId")
    fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>>

    @Query("SELECT * from $FORM_QUESTION_RESPONSE_TABLE where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :referenceId AND didiId = :didiId")
    fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int
    ): List<FormQuestionResponseEntity>

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

}