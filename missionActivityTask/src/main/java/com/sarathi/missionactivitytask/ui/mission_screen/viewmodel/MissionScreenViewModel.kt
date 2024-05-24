package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
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
    private val fetchDataUseCase: FetchDataUseCase,
    private val missionsUseCase: GetMissionsUseCase,
    @ApplicationContext val context: Context
) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionEntity>>(emptyList())
    val missionList: State<List<MissionEntity>> get() = _missionList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadGrantData()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initMissionScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _missionList.value = missionsUseCase.getAllMission()
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
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke(context)
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