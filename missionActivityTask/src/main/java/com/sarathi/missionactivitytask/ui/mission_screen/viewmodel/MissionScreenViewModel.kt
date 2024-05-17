package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.missionactivitytask.data.entities.MissionEntity
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
    private val missionsUseCase: GetMissionsUseCase
) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionEntity>>(emptyList())
    val missionList: State<List<MissionEntity>> get() = _missionList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initMissionScreen()
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
}