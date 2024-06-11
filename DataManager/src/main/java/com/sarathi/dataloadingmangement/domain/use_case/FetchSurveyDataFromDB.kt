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
        grantId: Int
    ): List<QuestionUiModel> {
        return repository.getQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId
        )
    }
}