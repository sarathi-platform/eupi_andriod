package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionEntity

interface MissionScreenRepository {
    suspend fun getMissions(): List<MissionEntity>?
    fun getLanguageId(): String

}
