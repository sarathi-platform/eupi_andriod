package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

interface FormQuestionResponseRepository {

    suspend fun getFormQuestionOptions(surveyId: Int,
                                       sectionId: Int,
                                       questionId: Int): List<OptionItemEntity>

    suspend fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
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
        didiId: Int
    )

    suspend fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity>

    suspend fun deleteFormQuestionResponseForReferenceId(referenceId: String)
}