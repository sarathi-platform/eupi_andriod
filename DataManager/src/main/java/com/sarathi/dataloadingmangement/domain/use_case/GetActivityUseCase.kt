package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.FORM_E
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.repository.GetActivityRepositoryImpl
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetActivityUseCase @Inject constructor(private val activityRepositoryImpl: GetActivityRepositoryImpl) {

    fun getActivities(missionId: Int): Flow<List<ActivityUiModel>> =
        activityRepositoryImpl.getActivity(missionId)

    suspend fun isActivityCompleted(missionId: Int, activityId: Int): Boolean {
        return activityRepositoryImpl.isActivityCompleted(
            missionId = missionId,
            activityId = activityId
        )
    }

    suspend fun isAllActivityCompleted(missionId: Int): Boolean {
        return activityRepositoryImpl.isAllActivityCompleted(
            missionId = missionId,
        )
    }

    suspend fun markMissionCompleteStatus(missionId: Int) {
        activityRepositoryImpl.updateMissionStatus(
            missionId = missionId,
            status = SurveyStatusEnum.COMPLETED.name
        )
    }

    suspend fun getActiveForm(
        formType: String = FORM_E
    ): List<ActivityFormUIModel> {
        return activityRepositoryImpl.getActiveForm(formType = formType)
    }

    suspend fun getTypeForActivity(missionId: Int, activityId: Int): String? {
        return activityRepositoryImpl.getTypeForActivity(missionId, activityId)
    }


}