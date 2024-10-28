package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel

interface GetConditionQuestionMappingsRepository {

    fun getConditionsUiModelForQuestions(
        surveyId: Int,
        sectionId: Int,
        questionIdList: List<Int>
    ): List<ConditionsUiModel>

}