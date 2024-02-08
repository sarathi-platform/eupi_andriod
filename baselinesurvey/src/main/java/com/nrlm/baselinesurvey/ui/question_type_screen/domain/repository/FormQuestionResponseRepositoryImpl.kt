package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.dao.FormQuestionResponseDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import javax.inject.Inject

class FormQuestionResponseRepositoryImpl @Inject constructor(
    private val optionItemDao: OptionItemDao,
    private val formQuestionResponseDao: FormQuestionResponseDao
) : FormQuestionResponseRepository {
    override suspend fun getFormQuestionOptions(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntity> {
        return optionItemDao.getSurveySectionQuestionOptions(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,)
    }

    override suspend fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormResponsesForQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            didiId = didiId
        )
    }

    override suspend fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>> {
        return formQuestionResponseDao.getFormResponsesForQuestionLive(surveyId, sectionId, questionId, didiId)
    }

    override suspend fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return formQuestionResponseDao.getFormResponsesForQuestionOption(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            referenceId = referenceId,
            didiId = didiId
        )
    }

    override suspend fun addFormResponseForQuestion(
        formQuestionResponseEntity: FormQuestionResponseEntity
    ) {
        formQuestionResponseDao.addFormResponse(formQuestionResponseEntity)
    }

    override suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int
    ) {
        return formQuestionResponseDao.updateOptionItemValue(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            optionId = optionId,
            selectedValue = selectedValue,
            referenceId = referenceId,
            didiId = didiId
        )
    }
}

