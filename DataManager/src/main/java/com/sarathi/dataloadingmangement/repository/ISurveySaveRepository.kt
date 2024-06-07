package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveySaveRepository {
    suspend fun saveSurveyAnswer(
        question: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String
    )

    fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String
    fun getUserIdentifier(): String
    suspend fun getAllSaveAnswer(
        surveyId: Int,
        taskId: Int,
        sectionId: Int
    ): List<SurveyAnswerEntity>
}