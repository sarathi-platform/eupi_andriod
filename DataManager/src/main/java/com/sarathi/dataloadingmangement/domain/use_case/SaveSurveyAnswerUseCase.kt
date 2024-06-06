package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository
import javax.inject.Inject

class SaveSurveyAnswerUseCase @Inject constructor(private val repository: ISurveySaveRepository) {
    suspend fun saveSurveyAnswer(questionUiModel: QuestionUiModel, subjectId: Int) {
        return repository.saveSurveyAnswer(questionUiModel, subjectId)
    }

    fun getAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        return repository.getSurveyAnswerForTag(taskId, subjectId, tagId)
    }

    fun getUserIdentifier(): String = repository.getUserIdentifier()
}