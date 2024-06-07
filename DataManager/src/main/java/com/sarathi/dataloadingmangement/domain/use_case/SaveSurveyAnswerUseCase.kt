package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository

class SaveSurveyAnswerUseCase(private val repository: ISurveySaveRepository) {
    suspend fun saveSurveyAnswer(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String
    ) {
        return repository.saveSurveyAnswer(
            questionUiModel,
            subjectId,
            taskId,
            referenceId = referenceId
        )
    }

    fun getAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        return repository.getSurveyAnswerForTag(taskId, subjectId, tagId)
    }

    suspend fun getAllSaveAnswer(
        surveyId: Int,
        taskId: Int,
        sectionId: Int
    ): List<SurveyAnswerEntity> {
        return repository.getAllSaveAnswer(
            taskId = taskId,
            surveyId = surveyId,
            sectionId = sectionId
        )
    }

    fun getUserIdentifier(): String = repository.getUserIdentifier()
}