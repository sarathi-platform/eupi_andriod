package com.nrlm.baselinesurvey.data.domain.useCase

import com.nrlm.baselinesurvey.data.domain.repository.UpdateBaselineStatusOnInitRepository
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class UpdateBaselineStatusOnInitUseCase @Inject constructor(
    private val updateBaselineStatusOnInitRepository: UpdateBaselineStatusOnInitRepository
) {

    private val tag = UpdateBaselineStatusOnInitUseCase::class.java.simpleName

    suspend fun invoke(onSuccess: (isSuccess: Boolean) -> Unit) {
        try {
            val mUserId = updateBaselineStatusOnInitRepository.getUserId()
            val mission = updateBaselineStatusOnInitRepository.getBaselineMission()
            mission.apply {
                updateBaselineStatusOnInitRepository.updateBaselineMissionStatusForGrant(
                    missionId = missionId, status = status,
                    userId = mUserId
                )

                val activities = updateBaselineStatusOnInitRepository.getActivitiesForMission(
                    mUserId, missionId
                )
                activities.forEach { activity ->
                    updateBaselineStatusOnInitRepository.updateBaselineActivityStatusForGrant(
                        missionId = missionId,
                        activityId = activity.activityId,
                        status = activity.status ?: SectionStatus.NOT_STARTED.name,
                        userId = mUserId
                    )
                }
                onSuccess(true)
            }
        } catch (ex: Exception) {
            CoreLogger.e(tag = tag, msg = "invoke: exception -> $ex", ex = ex, stackTrace = true)
            onSuccess(false)
        }

    }
}