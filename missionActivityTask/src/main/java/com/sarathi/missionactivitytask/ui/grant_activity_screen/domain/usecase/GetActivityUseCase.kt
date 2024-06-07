package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.usecase

import com.sarathi.dataloadingmangement.model.SurveyStatusEnum
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository.GetActivityRepositoryImpl
import javax.inject.Inject


class GetActivityUseCase @Inject constructor(private val activityRepositoryImpl: GetActivityRepositoryImpl) {

    suspend fun getActivities(missionId: Int): List<ActivityUiModel> =
        activityRepositoryImpl.getActivity(missionId)

    suspend fun isAllActivityCompleted(): Boolean {
        return activityRepositoryImpl.isAllActivityCompleted()
    }

    suspend fun markMissionCompleteStatus(missionId: Int) {
        activityRepositoryImpl.updateMissionStatus(
            missionId = missionId,
            status = SurveyStatusEnum.COMPLETED.name
        )
    }


}