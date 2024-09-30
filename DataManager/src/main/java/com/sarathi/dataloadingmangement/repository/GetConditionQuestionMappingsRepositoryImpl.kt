package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CorePrefRepo
import com.sarathi.dataloadingmangement.data.dao.ConditionsEntityDao
import com.sarathi.dataloadingmangement.data.dao.SourceTargetQuestionMappingEntityDao
import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel
import javax.inject.Inject

class GetConditionQuestionMappingsRepositoryImpl @Inject constructor(
    val corePrefRepo: CorePrefRepo,
    val sourceTargetQuestionMappingEntityDao: SourceTargetQuestionMappingEntityDao,
    val conditionsEntityDao: ConditionsEntityDao
) : GetConditionQuestionMappingsRepository {


    override fun getConditionsUiModelForQuestions(
        surveyId: Int,
        sectionId: Int,
        questionIdList: List<Int>
    ): List<ConditionsUiModel> {
        return sourceTargetQuestionMappingEntityDao.getConditionsForQuestion(
            userId = corePrefRepo.getUniqueUserIdentifier(),
            surveyId = surveyId,
            sectionId = sectionId,
            sourceQuestionIdList = questionIdList
        )
    }

}