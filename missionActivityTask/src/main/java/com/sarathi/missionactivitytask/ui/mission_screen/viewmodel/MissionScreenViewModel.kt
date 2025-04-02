package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.ALL_MISSION_FILTER_VALUE
import com.nudge.core.CoreObserverManager
import com.nudge.core.TabsCore
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.enums.ApiStatus
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.model.FilterType
import com.nudge.core.model.FilterUiModel
import com.nudge.core.ui.commonUi.CustomProgressState
import com.nudge.core.ui.commonUi.DEFAULT_PROGRESS_VALUE
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.usecase.BaselineV1CheckUseCase
import com.nudge.core.usecase.SyncMigrationUseCase
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.MissionFilterUtils
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MissionScreenViewModel @Inject constructor(
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    @ApplicationContext val context: Context,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val baselineV1CheckUseCase: BaselineV1CheckUseCase,
    private val syncMigrationUseCase: SyncMigrationUseCase,
    val missionFilterUtils: MissionFilterUtils
) : BaseViewModel() {
    private val _missionList = MutableStateFlow<List<MissionUiModel>>(emptyList())
    val missionList: StateFlow<List<MissionUiModel>> get() = _missionList
    private val _filterMissionList = MutableStateFlow<List<MissionUiModel>>(emptyList())

    val filterMissionList: StateFlow<List<MissionUiModel>> get() = _filterMissionList

    val tabs = TabsEnum.tabsList[TabsEnum.MissionTab] ?: listOf(
        SubTabs.OngoingMissions,
        SubTabs.CompletedMissions
    )
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    val filteredListLabel = mutableStateOf(BLANK_STRING)

    private var baseCurrentApiCount = 0 // only count api survey count
    private var TOTAL_API_CALL = 0
    var completedApi = mutableStateOf(0f)
    var failedApi = mutableStateOf(0f)
    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, com.nudge.core.BLANK_STRING)


    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
                loadAllData(false, DataLoadingTriggerType.FRESH_LOGIN)
                collectMissionListFromFlow()
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isGroupingApplied)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is CommonEvents.OnSubTabChanged -> {
                updateMissionListForSubTab(
                    tabs[TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)],
                    missionFilterUtils.selectedMissionFilter.value
                )
            }

            is CommonEvents.OnFilterUiModelSelected -> {
                missionFilterUtils.setSelectedMissionFilterValue(event.filterUiModel)
                updateMissionListForSubTab(
                    tabs[TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)],
                    missionFilterUtils.getSelectedMissionFilterValue()
                )
            }
        }
    }

    private fun updateMissionListForSubTab(subTabs: SubTabs, selectedFilter: FilterUiModel?) {
        _filterMissionList.value =
            getMissionListForSubTab(subTabs, selectedFilter)
        filteredListLabel.value = getFilteredListLabel()
    }

    private fun getMissionListForSubTab(
        subTabs: SubTabs,
        selectedFilter: FilterUiModel?
    ) = when (subTabs) {
        SubTabs.OngoingMissions -> {
            getMissionListForSelectedFilter(
                missionList.value.filter { it.missionStatus != SurveyStatusEnum.COMPLETED.name },
                selectedFilter
            )
        }

        SubTabs.CompletedMissions -> {
            getMissionListForSelectedFilter(
                missionList.value.filter { it.missionStatus == SurveyStatusEnum.COMPLETED.name },
                selectedFilter
            )
        }

        else -> {
            getMissionListForSelectedFilter(missionList.value, selectedFilter)
        }
    }

    private fun getMissionListForSelectedFilter(
        missionUiModelListForTab: List<MissionUiModel>,
        selectedFilter: FilterUiModel?
    ): List<MissionUiModel> {
        return selectedFilter?.let {

            when (selectedFilter.type) {
                is FilterType.ALL -> {
                    missionUiModelListForTab
                }

                is FilterType.GENERAL -> {
                    missionUiModelListForTab.filter { it.livelihoodType == null }
                }

                is FilterType.OTHER -> {
                    missionUiModelListForTab.filter {
                        it.programLivelihoodReferenceId?.contains(
                            (selectedFilter.type as FilterType.OTHER).filterValue
                        ) == true
                    }
                }
            }
        } ?: missionUiModelListForTab

    }

    override fun refreshData() {
        loadAllData(true, dataLoadingTriggerType = DataLoadingTriggerType.PULL_TO_REFRESH)
    }

    private fun performSearchQuery(searchTerm: String, searchApplied: Boolean) {
        val filteredList = ArrayList<MissionUiModel>()

        val updatedMissionList = getMissionListForSubTab(
            tabs[TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)],
            missionFilterUtils.getSelectedMissionFilterValue()
        )

        if (searchTerm.isNotEmpty()) {
            updatedMissionList.forEach { mission ->
                if (mission.description.lowercase().contains(searchTerm.lowercase())) {
                    filteredList.add(mission)
                }
            }
        } else {
            filteredList.addAll(updatedMissionList)
        }
        _filterMissionList.value = filteredList
    }

    private fun initMissionScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateMissionActivityStatus()
            updateStatusForBaselineMission {

            }
            checkAndUpdateDefaultTabAndCount()
            missionFilterUtils.createMissionFilters(missionList.value)
            delay(500)
            onEvent(
                CommonEvents.OnFilterUiModelSelected(
                    missionFilterUtils.getSelectedMissionFilterValue()
                )
            )
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun checkAndUpdateDefaultTabAndCount() {
        val currentSubTabIndex = TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)
        val newSubTabIndex = if (currentSubTabIndex == -1) {
            tabs.indexOf(SubTabs.OngoingMissions)
        } else {
            currentSubTabIndex
        }
        TabsCore.setSubTabIndex(TabsEnum.MissionTab.tabIndex, newSubTabIndex)
        updateCountMap()
    }

    private suspend fun updateCountMap() {
        countMap.put(
            SubTabs.OngoingMissions,
            missionList.value.filter { it.missionStatus != SurveyStatusEnum.COMPLETED.name }.size
        )
        countMap.put(
            SubTabs.CompletedMissions,
            missionList.value.filter { it.missionStatus == SurveyStatusEnum.COMPLETED.name }.size
        )
    }

    private fun loadAllData(
        isRefresh: Boolean,
        dataLoadingTriggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN
    ) {
        completedApi.value = 0f
        failedApi.value = 0f
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

        // To Delete events for version 1 to 2 sync migration
            syncMigrationUseCase.deleteEventsAfter1To2Migration()
            val customData: Map<String, Any> = mapOf(
                "propertiesName" to AppConfigKeysEnum.values().map { it.name }
            )
            fetchAllDataUseCase.invoke(
                customData = customData,
                screenName = "MissionScreen",
                dataLoadingTriggerType = dataLoadingTriggerType,
                isRefresh = isRefresh,
                onComplete = { isSucess, message ->
                    initMissionScreen()
                },
                totalNumberOfApi = { screenName, screenTotalApi ->
                    TOTAL_API_CALL = screenTotalApi
                },
                apiPerStatus = { apiName ->
                    updateProgress(apiUrl = apiName)
                },
                moduleName = "MAT"
            )

        }
    }

    suspend fun updateProgress(apiUrl: String) {
        val apiStatusData = fetchAllDataUseCase.getApiStatus(
            screenName = "MissionScreen", moduleName = "MAT", apiUrl
        )
        if (apiStatusData.status.equals(ApiStatus.SUCCESS.name)) {
            completedApi.value = completedApi.value.inc()
        } else {
            failedApi.value = failedApi.value.inc()
        }
        progressState.updateProgress(completedApi.value.toFloat() / TOTAL_API_CALL.toFloat())
        progressState.updateProgressText("${completedApi.value}/$TOTAL_API_CALL")
    }

    private fun collectMissionListFromFlow() {
        viewModelScope.launch {
            fetchAllDataUseCase.fetchMissionDataUseCase.getAllMission().catch {
            }.flowOn(Dispatchers.Main).collect()
            { missionListFlow ->
                _missionList.value = missionListFlow
                checkAndUpdateDefaultTabAndCount()

                onEvent(

                    CommonEvents.OnFilterUiModelSelected(
                        missionFilterUtils.getSelectedMissionFilterValue()
                    )
                )
            }
        }
    }

    // Temp method to be removed after baseline is migrated to Grant flow.
    private fun updateStatusForBaselineMission(onSuccess: (isSuccess: Boolean) -> Unit) {
        CoreObserverManager.notifyCoreObserversUpdateMissionActivityStatusOnGrantInit() {
            onSuccess(it)
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

    private suspend fun updateMissionActivityStatus(){
        val updateMissionStatusList = updateMissionActivityTaskStatusUseCase.reCheckMissionStatus()
        updateMissionStatusList.forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                surveyName = BLANK_STRING,
                missionEntity = it,
                isFromRegenerate = false
            )
        }
    }

    fun isMissionLoaded(
        missionId: Int,
        programId: Int,
        onComplete: (isDataLoaded: Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val isDataLoaded = fetchAllDataUseCase.fetchMissionDataUseCase.isMissionLoaded(
                missionId = missionId,
                programId = programId
            )
            withContext(Dispatchers.Main) {
            onComplete(isDataLoaded == 1)
        }
        }
    }

    fun getStateId() = fetchAllDataUseCase.getStateId()

    fun isBaselineV1Mission(missionName: String): Boolean {

        return baselineV1CheckUseCase.invoke(missionName)

    }

    fun getFilterUiModelForMission(programLivelihoodReferenceId: List<Int>?): FilterUiModel? {
        val livelihoodFilters =
            missionFilterUtils.getMissionFiltersList()
                .filterNot { it.type == FilterType.ALL || it.type == FilterType.GENERAL }
        return livelihoodFilters.find {
            programLivelihoodReferenceId?.contains(
                (it.type as FilterType.OTHER).filterValue
            ) == true
        }
    }

    fun getFilteredListLabel(): String {
        var filterLabel = missionFilterUtils.getSelectedMissionFilterValue().filterLabel.value()
        val filterValueCount = "(${filterMissionList.value.size})"
        if (filterLabel == BLANK_STRING) {
            filterLabel = ALL_MISSION_FILTER_VALUE
        }

        if (missionFilterUtils.getSelectedMissionFilterValue().type != FilterType.ALL && missionFilterUtils.getSelectedMissionFilterValue().type != FilterType.GENERAL) {
            val livelihoodType =
                missionFilterUtils.getSelectedMissionFilterValue().filterLabel
            filterLabel =
                "$livelihoodType ${translationHelper.getString(R.string.missions_filter_label_suffix)}"
        }

        filterLabel = "$filterLabel $filterValueCount"

        return filterLabel
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.MissionScreen
    }
}