package com.patsurvey.nudge.activities.backup.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase.MissionSummaryScreenUseCase
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
import com.nudge.core.BASELINE_MISSION_NAME
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.usecase.BaselineV1CheckUseCase
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.value
import com.patsurvey.nudge.activities.backup.domain.use_case.ReopenActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.LoaderState
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ActivityReopeningScreenViewModel @Inject constructor(
    private val reopenActivityUseCase: ReopenActivityUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val missionSummaryScreenUseCase: MissionSummaryScreenUseCase,
    private val fetchAppConfigFromCacheOrDbUseCase: FetchAppConfigFromCacheOrDbUsecase,
    private val baselineV1CheckUseCase: BaselineV1CheckUseCase,
    val  auditTrailUseCase: AuditTrailUseCase
) : BaseViewModel() {

    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState


    private val DEFAULT_MISSION_VALUE: Int = -1

    private val _missionList = mutableStateOf<List<MissionUiModel>>(emptyList())
    val missionList: State<List<MissionUiModel>> get() = _missionList

    private val _activityList = mutableStateOf<List<ActivityUiModel>>(emptyList())
    val activityList: State<List<ActivityUiModel>> get() = _activityList

    val selectedMissionId = mutableStateOf(DEFAULT_MISSION_VALUE)

    val selectedActivityIdList = mutableStateListOf<Int>()

    val isNextButtonForStep1Active = mutableStateOf(false)

    val isNextButtonForStep2Active = mutableStateOf(false)

    val isSelectAllEnabled = mutableStateOf(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                initMissionTab()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = loaderState.value.copy(isLoaderVisible = event.showLoader)
            }

            is InitDataEvent.InitActivityListState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                initActivityTab(event.missionId)

            }
        }
    }

    private fun initMissionTab() {
        ioViewModelScope {
            _missionList.value = fetchAllDataUseCase.fetchMissionDataUseCase.getAllMission()
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun initActivityTab(missionId: Int) {
        ioViewModelScope {

            val missionName =
                missionList.value.find { it.missionId == missionId }?.description.value()
            if (missionName.contains(
                    BASELINE_MISSION_NAME,
                    true
                ) && baselineV1CheckUseCase.invoke(missionName)
            ) {
                missionSummaryScreenUseCase.getMissionActivitiesFromDBUseCase.invoke(missionId)
                    ?.let {
                        _activityList.value = it.toActivityUiModelList()
                    }
            } else {
                _activityList.value = getActivityUseCase.getActivities(missionId)
            }

            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun reopenActivities(callBack: (result: Boolean) -> Unit) {
        ioViewModelScope {
            val selectedMission = missionList.value.find { it.missionId == selectedMissionId.value }
            selectedMission?.let {
                val result = reopenActivityUseCase.invoke(
                    missionId = selectedMissionId.value,
                    missionName = it.description,
                    activityIds = selectedActivityIdList.toList(),
                    isBaselineMission = it.description.equals(
                        BASELINE_MISSION_NAME,
                        true
                    )
                )

                withContext(mainDispatcher) {
                    callBack(result)
                }
            }
        }

    }

    fun onMissionSelected(missionUiModel: MissionUiModel) {
        selectedMissionId.value = missionUiModel.missionId
        selectedActivityIdList.clear()
        isSelectAllEnabled.value = false
        updateNextButtonForStep1Status()
    }

    fun isMissionSelected(missionId: Int): Boolean {
        return selectedMissionId.value == missionId
    }

    private fun updateNextButtonForStep1Status() {
        isNextButtonForStep1Active.value = selectedMissionId.value != DEFAULT_MISSION_VALUE
    }

    private fun updateNextButtonForStep2Status() {
        isNextButtonForStep2Active.value = !selectedActivityIdList.isEmpty()
    }

    fun onActivitySelected(
        activityUiModel: ActivityUiModel,
        isOnSelectAllClicked: Boolean = false
    ) {
        val tempList = selectedActivityIdList.toList()

        if (isOnSelectAllClicked) {
            onSelectAllClicked()

        } else {

            if (tempList.contains(activityUiModel.activityId)) {
                selectedActivityIdList.remove(activityUiModel.activityId)
            } else {
                selectedActivityIdList.add(activityUiModel.activityId)
            }
            isSelectAllEnabled.value =
                selectedActivityIdList.distinct().size == activityList.value.distinctBy { it.activityId }.size
        }
        updateNextButtonForStep2Status()
    }

    private fun onSelectAllClicked() {
        selectedActivityIdList.clear()

        if (isSelectAllEnabled.value) {
            selectedActivityIdList.addAll(activityList.value.map { it.activityId })
        }

        updateNextButtonForStep2Status()
    }

    fun isActivitySelected(activityId: Int): Boolean {
        return selectedActivityIdList.contains(activityId)
    }

}

private fun List<MissionActivityEntity>.toActivityUiModelList(): List<ActivityUiModel> {
    val activityUiModelList = ArrayList<ActivityUiModel>()

    this.forEach { missionActivityEntity ->
        val activityUiModel = ActivityUiModel(
            missionId = missionActivityEntity.missionId,
            activityId = missionActivityEntity.activityId,
            description = missionActivityEntity.activityName,
            activityType = missionActivityEntity.activityType,
            activityTypeId = ActivityTypeEnum.getActivityTypeIdFromName(missionActivityEntity.activityType),
            status = missionActivityEntity.status.value(),
            pendingTaskCount = 0,
            taskCount = 0
        )

        activityUiModelList.add(activityUiModel)

    }

    return activityUiModelList
}
