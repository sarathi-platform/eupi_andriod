package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionEntity

interface MissionScreenRepository {
    suspend fun getMissionsFromDB(): List<MissionEntity>?
    fun getLanguageId(): String


}
