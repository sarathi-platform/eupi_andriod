package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.UpdateMissionStatusUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.UpdateActivityStatusUseCase
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import javax.inject.Inject

class ReopenActivityUseCase @Inject constructor(
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val updateMissionStatusUseCase: UpdateMissionStatusUseCase, // For Baseline
    private val updateActivityStatusUseCase: UpdateActivityStatusUseCase, // For Baseline
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val reopenActivityEventHelperUseCase: ReopenActivityEventHelperUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
) {

    private val TAG = ReopenActivityUseCase::class.java.simpleName

    suspend operator fun invoke(
        missionId: Int,
        missionName: String,
        activityIds: List<Int>,
        isBaselineMission: Boolean = false
    ): Boolean {
        return if (isBaselineMission) {
            markBaselineMissionActivitiesInProgress(missionId, activityIds)
        } else {
            markPostBaselineMissionActivitiesInProgress(missionId, activityIds, missionName)
        }
    }

    private suspend fun markPostBaselineMissionActivitiesInProgress(
        missionId: Int,
        activityIds: List<Int>,
        missionName: String
    ): Boolean {
        try {
            updateMissionActivityTaskStatusUseCase.markActivitiesInProgress(
                missionId = missionId,
                activityIds = activityIds
            )
            reopenActivityEventHelperUseCase.getActivitiesForMission(
                missionId = missionId,
                activityIds = activityIds
            ).forEach { activity ->
                matStatusEventWriterUseCase.updateActivityStatus(
                    activityEntity = activity,
                    surveyName = missionName,
                    isFromRegenerate = false
                )
            }

            /**
             * Update Mission Status
             * */
            updateMissionActivityTaskStatusUseCase.markMissionInProgress(missionId)
            matStatusEventWriterUseCase.updateMissionStatus(missionId, missionName,false)
            return true
        } catch (e: Exception) {
            CoreLogger.e(
                tag = TAG,
                msg = "markPostBaselineMissionActivitiesInProgress -> Exception: ${e.message}",
                ex = e
            )
            return false
        }
    }

    private suspend fun markBaselineMissionActivitiesInProgress(
        missionId: Int,
        activityIds: List<Int>
    ): Boolean {

        try {
            updateActivityStatusUseCase.markActivitiesInProgress(
                missionId = missionId,
                activityIds = activityIds
            )
            reopenActivityEventHelperUseCase.getActivitiesForBaselineMission(
                missionId = missionId,
                activityIds = activityIds
            ).forEach { activity ->
                eventWriterHelperImpl.saveActivityStatusEvent(
                    missionId = activity.missionId,
                    activityId = activity.activityId,
                    activityStatus = SectionStatus.INPROGRESS.ordinal
                )
            }

            /**
             * Update Mission Status
             * */
            updateMissionStatusUseCase.invoke(missionId, SectionStatus.INPROGRESS)
            eventWriterHelperImpl.saveMissionStatusEvent(
                missionId,
                SectionStatus.INPROGRESS.ordinal
            )

            /**
             * Update Baseline Mission Status in GrantDb For Mission Screen
             * */
            updateMissionActivityTaskStatusUseCase.markMissionInProgress(missionId)

            return true
        } catch (e: Exception) {
            CoreLogger.e(
                tag = TAG,
                msg = "markBaselineMissionActivitiesInProgress -> Exception: ${e.message}",
                ex = e
            )
            return false
        }
    }

}