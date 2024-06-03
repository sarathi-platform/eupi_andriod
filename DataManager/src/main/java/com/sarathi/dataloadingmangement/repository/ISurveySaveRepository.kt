package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity

interface ISurveySaveRepository {
    suspend fun saveSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity)

}