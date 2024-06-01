package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveyRepository {

    suspend fun getQuestion(): List<QuestionUiModel>
}