package com.nrlm.baselinesurvey.ui.mission_summary_screen.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.MissionSummaryScreenUseCase
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionSummaryViewModel @Inject constructor(
    private val missionSummaryScreenUseCase: MissionSummaryScreenUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    val auditTrailUseCase: AuditTrailUseCase

) : BaseViewModel() {
    private val _activities = mutableStateOf<List<MissionActivityEntity>>(emptyList())
    public val activities: State<List<MissionActivityEntity>> get() = _activities

    val mission: MutableState<MissionEntity?> = mutableStateOf(null)

    override fun <T> onEvent(event: T) {
        when (event) {
            is EventWriterEvents.UpdateMissionStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    missionSummaryScreenUseCase.updateMissionStatusUseCase.invoke(
                        missionId = event.missionId,
                        status = event.status
                    )
                    val updateTaskStatusEvent =
                        eventWriterHelperImpl.createMissionStatusUpdateEvent(
                            missionId = event.missionId,
                            status = event.status
                        )
                    missionSummaryScreenUseCase.eventsWriterUserCase.invoke(
                        events = updateTaskStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }
        }

    }

    fun init(missionId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            eventWriterHelperImpl.recheckMATStatus()
            delay(200)
            missionSummaryScreenUseCase.getMissionActivitiesFromDBUseCase.invoke(missionId)?.let {
                _activities.value = it
            }
            missionSummaryScreenUseCase.updateMisisonState.updateMissionStatus(
                missionId,
                _activities.value
            )
            mission.value =
                missionSummaryScreenUseCase.getMissionActivitiesFromDBUseCase.getMission(missionId)
        }
    }

    fun getPendingDidiCountLive(activityId: Int): LiveData<Int> {
        return missionSummaryScreenUseCase.getPendingTaskCountLiveUseCase.invoke(activityId)
    }

}