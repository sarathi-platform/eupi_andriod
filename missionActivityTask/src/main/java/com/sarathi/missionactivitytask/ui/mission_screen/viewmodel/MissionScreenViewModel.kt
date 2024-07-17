package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.CoreObserverManager
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    @ApplicationContext val context: Context,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase
) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionUiModel>>(emptyList())
    val missionList: State<List<MissionUiModel>> get() = _missionList
    private val _filterMissionList = mutableStateOf<List<MissionUiModel>>(emptyList())

    val filterMissionList: State<List<MissionUiModel>> get() = _filterMissionList

    private var baseCurrentApiCount = 0 // only count api survey count
    private var TOTAL_API_CALL = 0

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadAllData(false)
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isSearchApplied)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    override fun refreshData() {
        loadAllData(true)
    }

    private fun performSearchQuery(searchTerm: String, searchApplied: Boolean) {
        val filteredList = ArrayList<MissionUiModel>()
        if (searchTerm.isNotEmpty()) {
            missionList.value.forEach { mission ->
                if (mission.description.lowercase().contains(searchTerm.lowercase())) {
                    filteredList.add(mission)
                }
            }
        } else {
            filteredList.addAll(missionList.value)
        }
        _filterMissionList.value = filteredList
    }

    private fun initMissionScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateMissionActivityStatus()
            _missionList.value = fetchAllDataUseCase.fetchMissionDataUseCase.getAllMission()
            _filterMissionList.value = _missionList.value
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun loadAllData(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchAllDataUseCase.invoke({ isSuccess, successMsg ->
                // Temp method to be removed after baseline is migrated to Grant flow.
                updateStatusForBaselineMission() { success ->
                    CoreLogger.i(
                        tag = "MissionScreenViewMode",
                        msg = "updateStatusForBaselineMission: success: $success"
                    )
                    initMissionScreen() // Move this out of the lambda block once the above method is removed
                }
            }, isRefresh = isRefresh)
        }
    }

    // Temp method to be removed after baseline is migrated to Grant flow.
    private fun updateStatusForBaselineMission(onSuccess: (isSuccess: Boolean) -> Unit) {
        CoreObserverManager.notifyCoreObserversUpdateMissionActivityStatusOnGrantInit() {
            onSuccess(it)
        }
    }

    private suspend fun updateLoaderEvent(callBack: () -> Unit) {
        if (baseCurrentApiCount == TOTAL_API_CALL) {
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
                callBack()
            }
        }
    }

    private suspend fun updateMissionActivityStatus(){
        val missionList = updateMissionActivityTaskStatusUseCase.reCheckMissionStatus()
        missionList.forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                surveyName = BLANK_STRING,
                missionEntity = it
            )
        }
    }
}