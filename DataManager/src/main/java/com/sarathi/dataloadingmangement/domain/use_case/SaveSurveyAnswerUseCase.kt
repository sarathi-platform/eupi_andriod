package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.repository.ISurveySaveRepository

class SaveSurveyAnswerUseCase(private val repository: ISurveySaveRepository) {
    suspend fun saveSurveyAnswer(surveyAnswerEntity: SurveyAnswerEntity) {
        return repository.saveSurveyAnswer(surveyAnswerEntity)
    }
}