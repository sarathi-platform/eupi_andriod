package com.nrlm.baselinesurvey.ui.mission_summary_screen.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.MissionSummaryScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionSummaryViewModel @Inject constructor(
    private val missionSummaryScreenUseCase: MissionSummaryScreenUseCase
) : BaseViewModel() {
    private val _activities = mutableStateOf<List<MissionActivityEntity>>(emptyList())
    public val activities: State<List<MissionActivityEntity>> get() = _activities
    override fun <T> onEvent(event: T) {
    }

    fun init(missionId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            missionSummaryScreenUseCase.getMissionActivitiesFromDBUseCase.invoke(missionId)?.let {
                _activities.value =
                    missionSummaryScreenUseCase.getActivityStateFromDBUseCase.getActivitiesStatus(
                        missionId,
                        it
                    )
            }
        }
    }

}