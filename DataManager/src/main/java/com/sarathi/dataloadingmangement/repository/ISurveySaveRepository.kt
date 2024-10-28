package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel

interface ISurveySaveRepository {
    suspend fun saveSurveyAnswer(
        question: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String,
        grantId: Int,
        grantType: String
    )

    fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String
    fun getUserIdentifier(): String
    suspend fun getAllSaveAnswer(
        activityConfigId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        grantId: Int
    ): List<SurveyAnswerFormSummaryUiModel>

    suspend fun deleteSurveyAnswer(
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int

    suspend fun getSurveyAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: String,
        activityConfigId: Int,
        referenceId: String
    ): String

    suspend fun getSurveyAnswerImageKeys(
        questionType: String
    ): List<SurveyAnswerEntity>?

    fun getTotalSavedFormResponsesCount(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        questionIds: List<Int>
    ): List<String>
}