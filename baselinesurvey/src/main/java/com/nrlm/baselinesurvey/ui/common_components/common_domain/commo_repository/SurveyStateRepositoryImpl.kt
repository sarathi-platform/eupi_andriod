package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

class SurveyStateRepositoryImpl(
    val prefBSRepo: PrefBSRepo,
    val surveyeeEntity: SurveyeeEntityDao,
    val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    val didiInfoDao: DidiInfoDao
): SurveyStateRepository {

    override suspend fun updateSurveyState(didiId: Int, surveyState: SurveyState) {
        surveyeeEntity.updateDidiSurveyStatus(surveyState.ordinal, didiId)
    }

    override suspend fun saveDidiInfo(didiInfoEntity: DidiInfoEntity) {
        didiInfoEntity.userId = prefBSRepo.getUniqueUserIdentifier()
        didiInfoDao.insertDidiInfo(didiInfoEntity)
    }

}