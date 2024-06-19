package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
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

    suspend fun getAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: String,
        referenceId: String
    ): String {
        return repository.getSurveyAnswerForFormTag(
            taskId = taskId,
            subjectId = subjectId,
            tagId = tagId,
            referenceId = referenceId
        )
    }

    suspend fun getAllSaveAnswer(
        surveyId: Int,
        taskId: Int,
        sectionId: Int
    ): List<SurveyAnswerFormSummaryUiModel> {
        return repository.getAllSaveAnswer(
            taskId = taskId,
            surveyId = surveyId,
            sectionId = sectionId
        )
    }

    fun getUserIdentifier(): String = repository.getUserIdentifier()

    suspend fun deleteSurveyAnswer(
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int {
        return repository.deleteSurveyAnswer(
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId,
            referenceId = referenceId
        )
    }

    suspend fun getSurveyAnswerImageKeys(
        questionType: String,
    ): List<OptionsUiModel>? {
        return repository.getSurveyAnswerImageKeys(questionType = questionType)
    }
}