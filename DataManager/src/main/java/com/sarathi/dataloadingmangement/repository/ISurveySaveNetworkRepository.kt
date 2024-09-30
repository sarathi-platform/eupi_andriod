package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.model.survey.response.QuestionAnswerResponseModel

interface ISurveySaveNetworkRepository {
    suspend fun getSurveyAnswerFromNetwork(surveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>>

    suspend fun getSurveyIds(): List<Int>
    fun saveSurveyAnswerToDb(surveyApiResponse: List<QuestionAnswerResponseModel>)
}