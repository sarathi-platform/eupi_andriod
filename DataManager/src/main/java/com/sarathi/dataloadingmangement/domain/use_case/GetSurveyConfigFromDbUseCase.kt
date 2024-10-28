package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.repository.GetSurveyConfigFromDbRepository
import javax.inject.Inject

class GetSurveyConfigFromDbUseCase @Inject constructor(
    private val getSurveyConfigFromDbRepository: GetSurveyConfigFromDbRepository
) {

    suspend operator fun invoke(
        missionId: Int,
        activityId: Int,
        surveyId: Int
    ): Map<Int, List<SurveyConfigEntity>> {
        val surveyConfigList =
            getSurveyConfigFromDbRepository.getSurveyConfig(missionId, activityId, surveyId)
        return surveyConfigList.groupBy { it.formId }
    }

    suspend operator fun invoke(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        formId: Int
    ): List<SurveyConfigEntity> {
        return getSurveyConfigFromDbRepository.getSurveyConfig(
            missionId,
            activityId,
            surveyId,
            formId
        )
    }

}