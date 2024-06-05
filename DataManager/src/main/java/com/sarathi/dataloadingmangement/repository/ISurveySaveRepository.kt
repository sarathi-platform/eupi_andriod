package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveySaveRepository {
    suspend fun saveSurveyAnswer(question: QuestionUiModel, subjectId: Int)
    fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String
    fun getUserIdentifier(): String
}