package com.nrlm.baselinesurvey.ui.mission_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.DELAY_2_SEC
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.MissionScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionScreenUseCase: MissionScreenUseCase,
    private val fetchDataUseCase: FetchDataUseCase,

    ) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionEntity>>(emptyList())
    private val missionList: State<List<MissionEntity>> get() = _missionList
    private val _filterMissionList = mutableStateOf<List<MissionEntity>>(emptyList())

    val filterMissionList: State<List<MissionEntity>> get() = _filterMissionList

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _missionTaskCountMap = mutableStateOf(mutableMapOf<Int, Int>())
    val missionTaskCountMap: State<Map<Int, Int>> get() = _missionTaskCountMap


    override fun <T> onEvent(event: T) {
        when (event) {
            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun init() {
        initMissionScreenList()
    }

    private fun initMissionScreenList() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            delay(DELAY_2_SEC)
            val mMissionList = missionScreenUseCase.getMissionListFromDbUseCase.invoke()
            mMissionList?.let {
                _missionList.value = it
                _filterMissionList.value = missionList.value
            }
            mMissionList?.forEach {
                val taskCountForMission =
                    missionScreenUseCase.getTaskDetailsFromDbUseCase.getTotalTaskCountForMission(it.missionId)
                _missionTaskCountMap.value.put(it.missionId, taskCountForMission)
            }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    override fun performSearchQuery(
        queryTerm: String, isFilterApplied: Boolean, fromScreen: String
    ) {
        val filteredList = ArrayList<MissionEntity>()
        if (queryTerm.isNotEmpty()) {
            missionList.value.forEach { mission ->
                if (mission.missionName.lowercase().contains(queryTerm.lowercase())) {
                    filteredList.add(mission)
                }
            }
        } else {
            filteredList.addAll(missionList.value)
        }
        _filterMissionList.value = filteredList
    }

    fun getPendingTaskCountForMissionLive(missionId: Int): LiveData<Int> {
        return missionScreenUseCase.getTaskDetailsFromDbUseCase.getPendingTaskCountForMission(
            missionId
        )
    }

    fun refreshData() {
        refreshData(fetchDataUseCase)
    }

}