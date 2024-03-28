package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.DidiInfoDao
import com.nrlm.baselinesurvey.database.dao.DidiSectionProgressEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

class SurveyStateRepositoryImpl(
    val prefRepo: PrefRepo,
    val surveyeeEntity: SurveyeeEntityDao,
    val didiSectionProgressEntityDao: DidiSectionProgressEntityDao,
    val didiInfoDao: DidiInfoDao
): SurveyStateRepository {

    override suspend fun updateSurveyState(didiId: Int, surveyState: SurveyState) {
        surveyeeEntity.updateDidiSurveyStatus(surveyState.ordinal, didiId)
    }

    override suspend fun saveDidiInfo(didiIntoEntity: DidiIntoEntity) {
        didiInfoDao.insertDidiInfo(didiIntoEntity)
    }

}