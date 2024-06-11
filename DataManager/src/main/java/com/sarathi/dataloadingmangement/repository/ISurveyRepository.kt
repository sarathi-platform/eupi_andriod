package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveyRepository {

    suspend fun getQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int
    ): List<QuestionUiModel>
}