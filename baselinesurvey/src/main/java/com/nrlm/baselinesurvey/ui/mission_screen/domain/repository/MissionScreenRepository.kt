package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.model.datamodel.MissionResponseModel

interface MissionScreenRepository {
    suspend fun getMissions(): List<MissionResponseModel>
}
