package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.SurveyEntity

interface MissionScreenRepository {
    suspend fun getSectionsList(): List<SurveyEntity>
}
