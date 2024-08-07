package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.SelectActivityOptionUiModel

interface IActivitySelectSurveyRepository {

    suspend fun getSelectActivityQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int
    ): List<SelectActivityOptionUiModel>
}