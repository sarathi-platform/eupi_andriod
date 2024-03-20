package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository

class GetFormQuestionResponseUseCase(private val repository: FormQuestionResponseRepository) {

    suspend operator fun invoke(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntity> {
        return repository.getFormQuestionOptions(surveyId = surveyId, sectionId = sectionId, questionId = questionId)
    }
    suspend fun getFormResponsesForQuestion(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return repository.getFormResponsesForQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            didiId = didiId
        )
    }

    suspend fun getFormResponsesForQuestionLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>> {
        return repository.getFormResponsesForQuestionLive(surveyId = surveyId, sectionId = sectionId, questionId = questionId, didiId = didiId)
    }

    suspend fun getFormResponsesForQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        referenceId: String,
        optionId: Int,
        didiId: Int,
    ): List<FormQuestionResponseEntity> {
        return repository.getFormResponsesForQuestionOption(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            referenceId = referenceId,
            didiId = didiId,
            optionId = optionId
        )
    }

    suspend fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity> {
        return repository.getFormResponseForReferenceId(referenceId)
    }

    suspend fun getFormQuestionForId(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): QuestionEntity? {
        return repository.getFormQuestionForId(surveyId, sectionId, questionId)
    }

    suspend fun getFormQuestionCountForSection(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return repository.getFormQuestionCountForSection(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId
        )
    }

    suspend fun getQuestionTag(surveyId: Int, sectionId: Int, questionId: Int): Int {
        return repository.getQuestionTag(surveyId, sectionId, questionId)
    }

}