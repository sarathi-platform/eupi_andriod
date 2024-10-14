package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel
import com.sarathi.dataloadingmangement.repository.GetConditionQuestionMappingsRepository
import javax.inject.Inject

class GetConditionQuestionMappingsUseCase @Inject constructor(
    private val getConditionQuestionMappingsRepository: GetConditionQuestionMappingsRepository
) {

    suspend operator fun invoke(
        surveyId: Int,
        sectionId: Int,
        questionIdList: List<Int>
    ): List<ConditionsUiModel> {

        return getConditionQuestionMappingsRepository.getConditionsUiModelForQuestions(
            surveyId = surveyId, sectionId = sectionId, questionIdList = questionIdList
        )

    }

}