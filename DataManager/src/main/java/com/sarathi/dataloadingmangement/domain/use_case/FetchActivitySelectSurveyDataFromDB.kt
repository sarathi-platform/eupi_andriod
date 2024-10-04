package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.SelectActivityOptionUiModel
import com.sarathi.dataloadingmangement.repository.IActivitySelectSurveyRepository
import javax.inject.Inject

class FetchActivitySelectSurveyDataFromDB @Inject constructor(
    private val repository: IActivitySelectSurveyRepository
) {
    suspend fun invoke(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int
    ): List<SelectActivityOptionUiModel> {
        return repository.getSelectActivityQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId
        )
    }
}