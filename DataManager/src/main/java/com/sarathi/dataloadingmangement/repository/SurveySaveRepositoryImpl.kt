package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import javax.inject.Inject

class SurveySaveRepositoryImpl @Inject constructor(val surveyAnswersDao: SurveyAnswersDao) :
    ISurveySaveRepository {

    override suspend fun saveSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity) {
        surveyAnswersDao.insertSurveyAnswer(surveyAnswerEntity)
    }


}