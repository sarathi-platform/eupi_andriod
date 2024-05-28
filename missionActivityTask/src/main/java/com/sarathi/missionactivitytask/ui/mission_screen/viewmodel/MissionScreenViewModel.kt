package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase,
    private val missionsUseCase: GetMissionsUseCase,

) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionUiModel>>(emptyList())
    val missionList: State<List<MissionUiModel>> get() = _missionList
    private val _filterMissionList = mutableStateOf<List<MissionUiModel>>(emptyList())

    val filterMissionList: State<List<MissionUiModel>> get() = _filterMissionList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadGrantData()
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
            _missionList.value = missionsUseCase.getAllMission()
            _filterMissionList.value = _missionList.value
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun loadGrantData() {
        fetchAllData {
            initMissionScreen()
        }
    }

    private fun fetchAllData(callBack: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                fetchMissionData(fetchDataUseCase) { callBack() }
            }
        } catch (ex: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            callBack()
        }

    }

    private fun fetchMissionData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            updateLoaderEvent(callBack)
        }
    }

    private suspend fun updateLoaderEvent(callBack: () -> Unit) {
        withContext(Dispatchers.Main) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            callBack()
        }
    }

}