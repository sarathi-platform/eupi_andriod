package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(val questionDao: QuestionEntityDao) :
    ISurveyRepository {
    override suspend fun getQuestion(): List<QuestionUiModel> {
        return questionDao.getQuestionOption(languageId = "en")
    }
}