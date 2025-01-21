package com.sarathi.missionactivitytask.ui.mission_screen.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nudge.core.ALL_MISSION_FILTER_VALUE
import com.nudge.core.CoreObserverManager
import com.nudge.core.GENERAL_MISSION_FILTER_VALUE
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.getFileNameFromURL
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.model.FilterType
import com.nudge.core.model.FilterUiModel
import com.nudge.core.toCamelCase
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.usecase.BaselineV1CheckUseCase
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.usecase.SyncMigrationUseCase
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
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
    @ApplicationContext val context: Context,
    private val updateMissionActivityTaskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase,
    private val baselineV1CheckUseCase: BaselineV1CheckUseCase,
    private val syncMigrationUseCase: SyncMigrationUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
) : BaseViewModel() {
    private val _missionList = mutableStateOf<List<MissionUiModel>>(emptyList())
    val missionList: State<List<MissionUiModel>> get() = _missionList
    private val _filterMissionList = mutableStateOf<List<MissionUiModel>>(emptyList())

    val filterMissionList: State<List<MissionUiModel>> get() = _filterMissionList

    val tabs = listOf<SubTabs>(SubTabs.OngoingMissions, SubTabs.CompletedMissions)
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    val missionFilterList: SnapshotStateList<FilterUiModel> = mutableStateListOf()

    val selectedMissionFilter: MutableState<FilterUiModel?> = mutableStateOf(null)

    val filteredListLabel = mutableStateOf(BLANK_STRING)

    private var baseCurrentApiCount = 0 // only count api survey count
    private var TOTAL_API_CALL = 0

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
                loadAllData(false)
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
                    selectedMissionFilter.value
                )
            }

            is CommonEvents.OnFilterUiModelSelected -> {
                selectedMissionFilter.value = event.filterUiModel
                updateMissionListForSubTab(
                    tabs[TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)],
                    selectedMissionFilter.value
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
                        it.livelihoodType.equals(
                            (selectedFilter.type as FilterType.OTHER).filterValue.toString(),
                            true
                        )
                    }
                }
            }
        } ?: missionUiModelListForTab

    }

    override fun refreshData() {
        loadAllData(true)
    }

    private fun performSearchQuery(searchTerm: String, searchApplied: Boolean) {
        val filteredList = ArrayList<MissionUiModel>()

        val updatedMissionList = getMissionListForSubTab(
            tabs[TabsCore.getSubTabForTabIndex(TabsEnum.MissionTab.tabIndex)],
            selectedMissionFilter.value
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

            TabsCore.setSubTabIndex(
                TabsEnum.MissionTab.tabIndex,
                tabs.indexOf(SubTabs.OngoingMissions)
            )
            _missionList.value = fetchAllDataUseCase.fetchMissionDataUseCase.getAllMission()
            createMissionFilters()
            onEvent(
                CommonEvents.OnFilterUiModelSelected(
                    selectedMissionFilter.value ?: FilterUiModel.getAllFilter(
                        ALL_MISSION_FILTER_VALUE,
                        filterLabel = "All Missions",
                        null
                    )
                )
            )


            updateCountMap()

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun createMissionFilters() {
        val filterList = ArrayList<FilterUiModel>()
        missionFilterList.clear()
        val livelihoods = getLivelihoodListFromDbUseCase.getLivelihoodListForFilterUi()

        filterList.add(
            FilterUiModel.getAllFilter(
                filterValue = ALL_MISSION_FILTER_VALUE,
                filterLabel = "All Missions",
                imageFileName = null
            )
        )
        filterList.add(
            FilterUiModel.getGeneralFilter(
                filterValue = GENERAL_MISSION_FILTER_VALUE,
                filterLabel = "General Missions",
                imageFileName = null
            )
        )

        with(livelihoods) {
            iterator().forEach {
                filterList.add(
                    FilterUiModel(
                        type = FilterType.OTHER(it.type),
                        filterValue = it.name,
                        filterLabel = it.name,
                        imageFileName = getFileNameFromURL(it.image.value())
                    )
                )
            }
        }
        withContext(Dispatchers.IO) {
            missionFilterList.addAll(filterList.distinctBy { it.filterValue })
        }
    }

    private fun updateCountMap() {
        countMap.put(
            SubTabs.OngoingMissions,
            missionList.value.filter { it.missionStatus != SurveyStatusEnum.COMPLETED.name }.size
        )
        countMap.put(
            SubTabs.CompletedMissions,
            missionList.value.filter { it.missionStatus == SurveyStatusEnum.COMPLETED.name }.size
        )
    }

    private fun loadAllData(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            // To Delete events for version 1 to 2 sync migration
            syncMigrationUseCase.deleteEventsAfter1To2Migration()
            fetchAllDataUseCase.invoke(isRefresh = isRefresh, onComplete = { isSucess, message ->
                initMissionScreen()
            })
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
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

    fun getFilterUiModelForMission(livelihoodType: String?): FilterUiModel? {
        val livelihoodFilters =
            missionFilterList.filterNot { it.type == FilterType.ALL || it.type == FilterType.GENERAL }
        return livelihoodFilters.find {
            livelihoodType.equals(
                (it.type as FilterType.OTHER).filterValue.toString(),
                true
            )
        }
    }

    fun getFilteredListLabel(): String {
        var filterLabel = selectedMissionFilter.value?.filterLabel.value()
        val filterValueCount = "(${filterMissionList.value.size})"
        if (filterLabel == BLANK_STRING) {
            filterLabel = ALL_MISSION_FILTER_VALUE
        }



        if (selectedMissionFilter.value?.type != FilterType.ALL && selectedMissionFilter.value?.type != FilterType.GENERAL) {
            val livelihoodType =
                (selectedMissionFilter.value?.type as FilterType.OTHER).filterValue.toString()
                    .toCamelCase()
            filterLabel = "$livelihoodType Missions"
        }

        filterLabel = "$filterLabel $filterValueCount"

        return filterLabel
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.MissionScreen
    }
}