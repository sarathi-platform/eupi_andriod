package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchPatQuestionRepository
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.QuestionListResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import javax.inject.Inject

class FetchPatQuestionRepositoryImpl @Inject constructor(
    private val languageListDao: LanguageListDao,
    private val questionListDao: QuestionListDao,
    private val apiService: ApiService,
    private val coreSharedPrefs: CoreSharedPrefs
) : FetchPatQuestionRepository {

    override fun getStateId(): Int {
        return coreSharedPrefs.getStateId()
    }

    override fun isBpcUser(): Boolean {
        return coreSharedPrefs.isUserBPC()
    }

    override suspend fun fetchQuestionListFromNetwork(getQuestionListRequest: GetQuestionListRequest): ApiResponseModel<QuestionListResponse> {
        return apiService.fetchQuestionListFromServer(getQuestionListRequest)
    }

    override suspend fun getAllQuestionsForLanguage(languageId: Int): List<QuestionEntity> {
        return questionListDao.getAllQuestionsForLanguage(languageId)
    }

    override suspend fun deleteQuestionTableForLanguage(languageId: Int) {
        questionListDao.deleteQuestionTableForLanguage(languageId)
    }

    override suspend fun saveQuestionsToDb(questionList: List<QuestionEntity>) {
        questionListDao.insertAll(questionList)
    }


}