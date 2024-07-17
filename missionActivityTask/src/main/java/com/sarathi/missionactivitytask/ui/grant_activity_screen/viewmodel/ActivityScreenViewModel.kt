package com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.repository.IMATStatusEventRepository
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
class ActivityScreenViewModel @Inject constructor(
    private val getActivityUseCase: GetActivityUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase
) : BaseViewModel() {
    var missionId: Int = 0
    var isMissionCompleted: Boolean = false
    private val _activityList = mutableStateOf<List<ActivityUiModel>>(emptyList())
    val activityList: State<List<ActivityUiModel>> get() = _activityList
    val isButtonEnable = mutableStateOf<Boolean>(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initActivityScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initActivityScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _activityList.value = getActivityUseCase.getActivities(missionId)
            getContentValue(_activityList.value)
            checkButtonValidation()
            updateActivityStatus()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun setMissionDetail(missionId: Int, isMissionCompleted: Boolean) {
        this.missionId = missionId
        this.isMissionCompleted = isMissionCompleted
    }

    fun isFilePathExists(filePath: String): Boolean {
        return fetchContentUseCase.isFilePathExists(filePath)
    }

    private suspend fun checkButtonValidation() {
        isButtonEnable.value =
            !isMissionCompleted && getActivityUseCase.isAllActivityCompleted(missionId = missionId)
    }

    fun markMissionCompleteStatus() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            taskStatusUseCase.markMissionCompleted(missionId = missionId)
            eventWriterUseCase.updateMissionStatus(missionId = missionId, surveyName = "CSG")

        }
    }

    fun getContentValue(actvityUiList: List<ActivityUiModel>) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            actvityUiList.forEach {
                it.icon = it.icon?.let { it1 -> fetchContentUseCase.getContentValue(it1) }
            }
        }
    }

    private suspend fun updateActivityStatus(){
        val activityList = updateMissionActivityTaskStatusUseCase.reCheckActivityStatus()
        val missionList = updateMissionActivityTaskStatusUseCase.reCheckMissionStatus()
        activityList.forEach {
            matStatusEventWriterUseCase.updateActivityStatus(
                surveyName = BLANK_STRING,
                activityEntity = it
            )
        }
        missionList.forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                surveyName = BLANK_STRING,
                missionEntity = it
            )
        }
    }
}