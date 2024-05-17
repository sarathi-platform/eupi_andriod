package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity

interface IActivityRepository {
    suspend fun getActivity(): List<MissionActivityEntity>

}