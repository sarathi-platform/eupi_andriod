package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.usecase

import com.sarathi.dataloadingmangement.data.entities.Activity
import com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository.GetActivityRepositoryImpl
import javax.inject.Inject


class GetActivityUseCase @Inject constructor(private val activityRepositoryImpl: GetActivityRepositoryImpl) {

    suspend fun getActivities(): List<Activity> = activityRepositoryImpl.getActivity()

}