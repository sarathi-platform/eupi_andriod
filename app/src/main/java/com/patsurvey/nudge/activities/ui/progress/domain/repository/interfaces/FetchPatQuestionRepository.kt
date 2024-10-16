package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.QuestionListResponse

interface FetchPatQuestionRepository {

    fun getStateId(): Int

    fun isBpcUser(): Boolean

    suspend fun fetchQuestionListFromNetwork(getQuestionListRequest: GetQuestionListRequest): ApiResponseModel<QuestionListResponse>

    suspend fun getAllQuestionsForLanguage(languageId: Int): List<QuestionEntity>

    suspend fun deleteQuestionTableForLanguage(languageId: Int)

    suspend fun saveQuestionsToDb(questionList: List<QuestionEntity>)
}