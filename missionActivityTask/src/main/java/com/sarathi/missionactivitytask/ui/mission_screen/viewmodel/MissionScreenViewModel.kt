package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.domain.DataLoadingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
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
    private val fetchDataUseCase: DataLoadingUseCase,
    private val missionsUseCase: GetMissionsUseCase,
    @ApplicationContext val context: Context,
    private val contentDownloaderUseCase: ContentDownloaderUseCase

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
                loadAllData()
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

    private fun loadAllData() {
        TOTAL_API_CALL = 2
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchAllDataUseCase.invoke { isSuccess, successMsg ->
                initMissionScreen()
            }
        }
    }

    private fun fetchAllData(callBack: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                fetchContentData(fetchDataUseCase) {}
                contentDataDownloader(contentDownloaderUseCase) {}
                fetchMissionData(fetchDataUseCase) { callBack() }
                fetchDataUseCase.fetchSurveyDataFromNetworkUseCase.invoke()
            }
        } catch (ex: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            callBack()
        }

    }

    private fun fetchMissionData(dataLoadingUseCase: DataLoadingUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            dataLoadingUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            initMissionScreen()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchContentData(dataLoadingUseCase: DataLoadingUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            dataLoadingUseCase.fetchContentDataFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun contentDataDownloader(
        contentDownloaderUseCase: ContentDownloaderUseCase,
        callBack: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            contentDownloaderUseCase.contentDownloader()
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
}