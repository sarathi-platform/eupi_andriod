package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity

interface FormQuestionResponseRepository {

    suspend fun getFormQuestionOptions(
        surveyId: Int,
        sectionId: Int,
        questionId: Int, selectDefaultLanguage: Boolean = false
    ): List<OptionItemEntity>

    suspend fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    suspend fun getFormResponsesForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    suspend fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>>

    suspend fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int,
        optionId: Int
    ): List<FormQuestionResponseEntity>

    suspend fun addFormResponseForQuestion(
        formQuestionResponseEntity: FormQuestionResponseEntity
    )

    suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int,
        selectedValueIds: List<Int>
    )

    suspend fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity>

    suspend fun deleteFormQuestionResponseForReferenceId(referenceId: String)
    suspend fun saveFormsIntoDB(form: List<FormQuestionResponseEntity>)

    suspend fun deleteFormQuestionResponseForOption(
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        surveyeeId: Int,
        referenceId: String
    )

    suspend fun updateFromListItemIntoDb(
        formQuestionResponseEntity: FormQuestionResponseEntity
    )

    suspend fun getOptionItem(formQuestionResponseEntity: FormQuestionResponseEntity): Int
    suspend fun getFormQuestionForId(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): QuestionEntity?

    suspend fun getFormQuestionCountForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity>

    suspend fun getQuestionTag(surveyId: Int, sectionId: Int, questionId: Int): Int
    suspend fun getContentFromDB(contentKey: String): ContentEntity

    fun getBaseLineUserId(): String
}