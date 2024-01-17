package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.SurveyEntityDao
import com.nrlm.baselinesurvey.database.entity.SurveyEntity

class MissionScreenRepositoryImpl(
    private val surveyEntityDao: SurveyEntityDao,
) : MissionScreenRepository {
    override suspend fun getSectionsList(): List<SurveyEntity> {
        return surveyEntityDao.getAllSurvey()
    }
}