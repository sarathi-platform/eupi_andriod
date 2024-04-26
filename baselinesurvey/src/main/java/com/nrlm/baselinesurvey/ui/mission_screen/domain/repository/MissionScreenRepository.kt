package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.MissionEntity

interface MissionScreenRepository {
    suspend fun getMissionsFromDB(): List<MissionEntity>?

    //    fun getLanguageId(): String
    fun getTotalTaskCountForMission(missionId: Int): Int

    fun getPendingTaskCountLiveForMission(missionId: Int): LiveData<Int>

    fun getPendingActivityCountForMissionLive(missionId: Int): LiveData<Int>

    fun getTotalActivityCountForMission(missionId: Int): Int //TODO @Ankit Kumar Jain please add userId for this method.

    fun getBaseLineUserId(): String

}
