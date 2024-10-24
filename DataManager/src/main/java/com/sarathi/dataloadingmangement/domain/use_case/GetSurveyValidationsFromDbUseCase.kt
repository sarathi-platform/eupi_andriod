package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.model.response.SurveyValidations
import com.sarathi.dataloadingmangement.repository.GetSurveyValidationsFromDbRepository
import javax.inject.Inject

class GetSurveyValidationsFromDbUseCase @Inject constructor(
    private val getSurveyValidationsFromDbRepository: GetSurveyValidationsFromDbRepository
) {

    fun invoke(surveyId: Int, sectionId: Int): List<SurveyValidations>? {

        return getSurveyValidationsFromDbRepository.getSurveyValidationsForSurvey(surveyId)
            ?.filter { it.sectionId == sectionId }

    }

}