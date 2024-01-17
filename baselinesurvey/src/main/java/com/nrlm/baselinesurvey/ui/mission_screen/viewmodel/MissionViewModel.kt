package com.nrlm.baselinesurvey.ui.mission_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.SurveyEntity
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
    private val _sectionsList = mutableStateOf<List<SurveyEntity>>(emptyList())
    val sectionsList: State<List<SurveyEntity>> get() = _sectionsList

    override fun <T> onEvent(event: T) {
    }

    fun init() {
        initMissionScreenList()
    }

    private fun initMissionScreenList() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _sectionsList.value = missionScreenUseCase.getSectionUseCase.invoke()
        }
    }
}