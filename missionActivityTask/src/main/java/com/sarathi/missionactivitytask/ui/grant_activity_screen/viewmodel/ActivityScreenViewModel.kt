package com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.CoreObserverManager
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
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
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase
) : BaseViewModel() {
    var missionId: Int = 0
    var isMissionCompleted: Boolean = false
    var programId: Int = 0
    private val _activityList = mutableStateOf<List<ActivityUiModel>>(emptyList())
    val activityList: State<List<ActivityUiModel>> get() = _activityList
    val isButtonEnable = mutableStateOf<Boolean>(false)
    var showDialog = mutableStateOf<Boolean>(false)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadMissionRelatedData(isRefresh = false)
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

    private fun loadMissionRelatedData(isRefresh: Boolean) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            fetchAllDataUseCase.fetchMissionRelatedData(
                missionId = missionId,
                programId = programId,
                isRefresh = false,
                { isSuccess, successMsg ->

                    initActivityScreen()
                })
        }
    }

    fun setMissionDetail(missionId: Int, isMissionCompleted: Boolean, programId: Int) {
        this.missionId = missionId
        this.isMissionCompleted = isMissionCompleted
        this.programId = programId
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
            eventWriterUseCase.updateMissionStatus(
                missionId = missionId,
                surveyName = "CSG",
                isFromRegenerate = false
            )

        }
    }

    fun getContentValue(actvityUiList: List<ActivityUiModel>) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            actvityUiList.forEach {
                it.icon = it.icon?.let { it1 ->
                    fetchContentUseCase.getContentValue(
                        it1,
                        DEFAULT_LANGUAGE_CODE
                    )
                }
            }
        }
    }

    private suspend fun updateActivityStatus() {
        val updateActivityStatusList =
            updateMissionActivityTaskStatusUseCase.reCheckActivityStatus()
        val updateMissionStatusList = updateMissionActivityTaskStatusUseCase.reCheckMissionStatus()
        updateActivityStatusList.forEach {
            matStatusEventWriterUseCase.updateActivityStatus(
                surveyName = BLANK_STRING,
                activityEntity = it,
                isFromRegenerate = false
            )
        }
        updateMissionStatusList.forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                surveyName = BLANK_STRING,
                missionEntity = it,
                isFromRegenerate = false
            )
        }
    }

    private fun updateStatusForBaselineMission(onSuccess: (isSuccess: Boolean) -> Unit) {
        CoreObserverManager.notifyCoreObserversUpdateMissionActivityStatusOnGrantInit() {
            onSuccess(it)
        }
    }
    override fun refreshData() {
        loadMissionRelatedData(isRefresh = true)

    }
}