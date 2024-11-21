package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveyRepository
import javax.inject.Inject

class FetchSurveyDataFromDB @Inject constructor(
    private val repository: ISurveyRepository
) {
    suspend fun invoke(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        missionId: Int,
        activityId: Int,
        isFromRegenerate: Boolean = false
    ): List<QuestionUiModel> {
        return repository.getQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId,
            missionId = missionId,
            activityId = activityId,
            isFromRegenerate = isFromRegenerate
        )
    }

    suspend fun invokeFormQuestions(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        formId: Int,
        missionId: Int,
        activityId: Int
    ): List<QuestionUiModel> {
        return repository.getFormQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId,
            formId = formId,
            missionId = missionId,
            activityId = activityId
        )
    }
}