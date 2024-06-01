package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveyRepository

class FetchSurveyDataFromDB(
    private val repository: ISurveyRepository
) {
    suspend operator fun invoke(): List<QuestionUiModel> {
        return repository.getQuestion()
    }
}