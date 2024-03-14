package com.nrlm.baselinesurvey.ui.mission_summary_screen.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.MissionSummaryScreenUseCase
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionSummaryViewModel @Inject constructor(
    private val missionSummaryScreenUseCase: MissionSummaryScreenUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {
    private val _activities = mutableStateOf<List<MissionActivityEntity>>(emptyList())
    public val activities: State<List<MissionActivityEntity>> get() = _activities
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
            missionSummaryScreenUseCase.getMissionActivitiesFromDBUseCase.invoke(missionId)?.let {
                _activities.value = it
            }
            missionSummaryScreenUseCase.updateMisisonState.updateMissionStatus(
                missionId,
                _activities.value
            )
        }
    }

}