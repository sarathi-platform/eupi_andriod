package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.CoreObserverManager
import com.nudge.core.usecase.BaselineV1CheckUseCase
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.usecase.SyncMigrationUseCase
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
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase,
    private val baselineV1CheckUseCase: BaselineV1CheckUseCase,
    private val syncMigrationUseCase: SyncMigrationUseCase
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
                performSearchQuery(event.searchTerm, event.isGroupingApplied)
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
            updateStatusForBaselineMission {

            }
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
            // To Delete events for version 1 to 2 sync migration
            syncMigrationUseCase.deleteEventsAfter1To2Migration()
            fetchAllDataUseCase.invoke(isRefresh = isRefresh, onComplete = { isSucess, message ->
                initMissionScreen()
            }
            )
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
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
        val updateMissionStatusList = updateMissionActivityTaskStatusUseCase.reCheckMissionStatus()
        updateMissionStatusList.forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                surveyName = BLANK_STRING,
                missionEntity = it,
                isFromRegenerate = false
            )
        }
    }

    fun isMissionLoaded(
        missionId: Int,
        programId: Int,
        onComplete: (isDataLoaded: Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val isDataLoaded = fetchAllDataUseCase.fetchMissionDataUseCase.isMissionLoaded(
                missionId = missionId,
                programId = programId
            )
            withContext(Dispatchers.Main) {
            onComplete(isDataLoaded == 1)
        }
        }
    }

    fun getStateId() = fetchAllDataUseCase.getStateId()

    fun isBaselineV1Mission(missionName: String): Boolean {

        return baselineV1CheckUseCase.invoke(missionName)

    }
}