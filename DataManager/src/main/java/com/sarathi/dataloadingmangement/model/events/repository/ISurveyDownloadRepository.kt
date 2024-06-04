package com.sarathi.dataloadingmangement.model.events.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel

interface ISurveyDownloadRepository {
    suspend fun fetchSurveyFromNetwork(surveyRequestBodyModel: SurveyRequest): ApiResponseModel<SurveyResponseModel>
    fun saveSurveyToDb(surveyApiResponse: SurveyResponseModel)

}