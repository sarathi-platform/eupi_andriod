package com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.nudge.core.CoreObserverManager
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.TabsCore
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.domain.FetchMissionDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.ContentDownloaderUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchInfoUiModelUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.util.MissionFilterUtils
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_SCREEN
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_MODULE
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ActivityScreenViewModel @Inject constructor(
    //private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchInfoUiModelUseCase: FetchInfoUiModelUseCase,
    private val missionFilterUtils: MissionFilterUtils,
    private val fetchMissionDataUseCase: FetchMissionDataUseCase,
    private val contentDownloaderUseCase: ContentDownloaderUseCase
) : BaseViewModel() {
    var missionId: Int = 0
    var isMissionCompleted: Boolean = false
    var programId: Int = 0
    private val _activityList = MutableStateFlow<List<ActivityUiModel>>(emptyList())
    val activityList: StateFlow<List<ActivityUiModel>> get() = _activityList
    val isButtonEnable = mutableStateOf<Boolean>(false)
    var showDialog = mutableStateOf<Boolean>(false)
    var missionInfoUIModel by mutableStateOf(MissionInfoUIModel.getDefaultValue())

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
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


            getContentValue(_activityList.value)
            checkButtonValidation()
            updateActivityStatus()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

        }
    }

    private fun loadMissionRelatedData(isRefresh: Boolean) {
        viewModelScope.launch {
            getActivityUseCase.getActivities(missionId).collect() {
                _activityList.value = it
            }
        }

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val missionDetails = fetchInfoUiModelUseCase.fetchMissionInfo(missionId)
            withContext(mainDispatcher) {
                missionInfoUIModel = missionDetails
            }
            onEvent(LoaderEvent.UpdateLoaderState(true))
//            if (isRefresh || fetchMissionDataUseCase.isMissionLoaded(
//                    missionId = missionId,
//                    programId
//                ) == 0
//            ) {
            if (true) {
                val customData: Map<String, Any> = mapOf(
                    "MissionId" to missionId,
                    "ProgramId" to programId
                )
                loadAllData(
                    screenName = ACTIVITY_SCREEN,
                    moduleName = MAT_MODULE,
                    customData = customData,
                    dataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN
                )
                CoroutineScope(Dispatchers.IO).launch {
                    contentDownloaderUseCase.contentDownloader()
                    contentDownloaderUseCase.surveyRelateContentDownlaod()
                }
                // fetchMissionDataUseCase.setMissionLoaded(missionId = missionId, programId)
            }
            onEvent(LoaderEvent.UpdateLoaderState(false))
//            fetchAllDataUseCase.invoke(
//                customData = customData,
//                screenName = "ActivityScreen",
//                dataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
//                isRefresh = isRefresh,
//                onComplete = { isSucess, message ->
//                    initActivityScreen()
//                },
//                totalNumberOfApi = { screenName, screenTotalApi ->
//                    totalApiCall.value = screenTotalApi
//                },
//                apiPerStatus = { apiName, requestPayload ->
//                    val apiStatusData = fetchAllDataUseCase.getApiStatus(
//                        screenName = "ActivityScreen",
//                        moduleName = MAT_MODULE,
//                        apiUrl = apiName,
//                        requestPayload = requestPayload
//                    )
//                    apiStatusData?.let { updateProgress(apiStatusData = it) }
//                },
//                moduleName = MAT_MODULE
//            )

//            fetchAllDataUseCase.fetchMissionRelatedData(
//                missionId = missionId,
//                programId = programId,
//                screenName = "ActivityScreen",
//                dataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
//                moduleName = "MAT",
//                isRefresh = isRefresh,
//                onComplete =
//                { isSuccess, successMsg ->
//                    initActivityScreen()
//                }
//            )

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
            updateMissionActivityTaskStatusUseCase.reCheckActivityStatus(missionId)
        updateActivityStatusList.forEach {
            matStatusEventWriterUseCase.updateActivityStatus(
                surveyName = BLANK_STRING,
                activityEntity = it,
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
        onEvent(LoaderEvent.UpdateLoaderState(true))

        loadMissionRelatedData(isRefresh = true)

    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.ActivityScreen
    }

    fun updateMissionFilterAndTab() {
        missionFilterUtils.updateMissionFilterOnUserAction(missionInfoUIModel)
        TabsCore.setSubTabIndex(TabsEnum.MissionTab.tabIndex,
            TabsEnum.tabsList[TabsEnum.MissionTab]?.indexOf(SubTabs.CompletedMissions)
                .value(NUMBER_ZERO)
        )
    }
}