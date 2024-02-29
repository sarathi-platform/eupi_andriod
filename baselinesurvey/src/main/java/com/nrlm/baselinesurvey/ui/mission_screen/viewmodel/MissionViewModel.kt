package com.nrlm.baselinesurvey.ui.mission_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.MissionScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionScreenUseCase: MissionScreenUseCase
) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionEntity>>(emptyList())
    private val missionList: State<List<MissionEntity>> get() = _missionList
    private val _filterMissionList = mutableStateOf<List<MissionEntity>>(emptyList())

    val filterMissionList: State<List<MissionEntity>> get() = _filterMissionList


    override fun <T> onEvent(event: T) {
        when (event) {
            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }
        }
    }

    fun init() {
        initMissionScreenList()
    }

    private fun initMissionScreenList() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            missionScreenUseCase.getMissionListFromDbUseCase.invoke()?.let {
                _missionList.value = it
                _filterMissionList.value = it
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
}