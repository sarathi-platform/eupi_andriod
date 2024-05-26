package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityEntity

interface IActivityRepository {
    suspend fun getActivity(): List<ActivityEntity>

}