package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveyRepository

class FetchSurveyDataFromDB(
    private val repository: ISurveyRepository
) {
    suspend operator fun invoke(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int
    ): List<QuestionUiModel> {
        return repository.getQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId
        )
    }
}