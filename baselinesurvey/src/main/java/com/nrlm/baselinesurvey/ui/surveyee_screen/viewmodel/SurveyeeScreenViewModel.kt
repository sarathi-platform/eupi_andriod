package com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DIDI_LIST
import com.nrlm.baselinesurvey.NO_TOLA_TITLE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.ApiStatusEvent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.SurveyeeScreenUseCase
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListEvents
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.FilterListState
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyeeScreenViewModel @Inject constructor(
    private val surveyeeScreenUseCase: SurveyeeScreenUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    private val fetchDataUseCase: FetchDataUseCase
) : BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _surveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val surveyeeListState: State<List<SurveyeeCardState>> get() = _surveyeeListState

    private val _thisWeekSurveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val thisWeekSurveyeeListState: State<List<SurveyeeCardState>> get() = _thisWeekSurveyeeListState

    private var _filteredSurveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val filteredSurveyeeListState: State<List<SurveyeeCardState>> get() = _filteredSurveyeeListState

    private val _thisWeekFilteredSurveyeeListState =
        mutableStateOf(mutableListOf<SurveyeeCardState>())
    val thisWeekFilteredSurveyeeListState: State<List<SurveyeeCardState>> get() = _thisWeekFilteredSurveyeeListState

    var tolaMapList by mutableStateOf(mapOf<String, List<SurveyeeCardState>>())

    private var _tolaMapSurveyeeListState = mutableStateOf(mapOf<String, List<SurveyeeCardState>>())
    val tolaMapSurveyeeListState: State<Map<String, List<SurveyeeCardState>>> get() = _tolaMapSurveyeeListState

    private var _filteredTolaMapSurveyeeListState =
        mutableStateOf(mapOf<String, List<SurveyeeCardState>>())
    val filteredTolaMapSurveyeeListState: State<Map<String, List<SurveyeeCardState>>> get() = _filteredTolaMapSurveyeeListState

    private var _thisWeekTolaMapSurveyeeListState =
        mutableStateOf(mapOf<String, List<SurveyeeCardState>>())

    val thisWeekTolaMapSurveyeeListState: State<Map<String, List<SurveyeeCardState>>> get() = _thisWeekTolaMapSurveyeeListState

    val checkedItemsState = mutableStateOf(mutableSetOf<Int>())

    val showMoveDidisBanner = mutableStateOf(false)
    var isEnableNextBTn =
        mutableStateOf(false)

    val isFilterAppliedState = mutableStateOf(FilterListState())
    val pageFrom = mutableStateOf(ALL_TAB)
    var missionId: Int = 0
    var activityName: String = ""
    var activityId: Int = 0


    @SuppressLint("SuspiciousIndentation")
    fun init(missionId: Int, activityName: String, activityId: Int) {
        this.missionId = missionId
        this.activityName = activityName
        this.activityId = activityId
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val surveyeeListFromDb =
                surveyeeScreenUseCase.getSurveyeeListUseCase.invoke(missionId, activityName)
            if (_surveyeeListState.value.isNotEmpty()) {
                _surveyeeListState.value.clear()
            }
            surveyeeListFromDb.forEach { surveyeeEntity ->
                var surveyeeState = SurveyeeCardState(
                    surveyeeDetails = surveyeeEntity,
                    imagePath = BLANK_STRING,
                    subtitle = surveyeeEntity.dadaName,
                    address = getSurveyeeAddress(surveyeeEntity),
                    activityName = activityName,
                    surveyState = SurveyState.getStatusFromOrdinal(surveyeeEntity.surveyStatus)
                )
                if (surveyeeEntity.crpImageLocalPath.isNotEmpty()) {
                    val imagePath = surveyeeEntity.crpImageLocalPath.split("|").first()
                    surveyeeState = surveyeeState.copy(
                        imagePath = imagePath
                    )
                }

                _surveyeeListState.value.add(surveyeeState)
            }
            _filteredSurveyeeListState.value = _surveyeeListState.value
            surveyeeScreenUseCase.getActivityStateFromDBUseCase.getActivitiesStatus(
                activityId = activityId,
                _surveyeeListState.value
            )
            isEnableNextBTn.value =
                filteredSurveyeeListState.value.filter { it.surveyState != SurveyState.COMPLETED }
                    .isEmpty()

            filterList(pageFrom.value)


            /*if (_thisWeekSurveyeeListState.value.isNotEmpty()) {
                _thisWeekSurveyeeListState.value.clear()
            }
            surveyeeListFromDb.filter { it.movedToThisWeek }.forEach { surveyeeEntity ->
                val surveyeeState = SurveyeeCardState(
                    surveyeeDetails = surveyeeEntity,
                    imagePath = surveyeeEntity.crpImageName,
                    subtitle = surveyeeEntity.dadaName,
                    address = getSurveyeeAddress(surveyeeEntity),
                    surveyState = SurveyState.getStatusFromOrdinal(surveyeeEntity.surveyStatus)
                )
                _thisWeekSurveyeeListState.value.add(surveyeeState)
            }
//            }
            _thisWeekFilteredSurveyeeListState.value = _thisWeekSurveyeeListState.value*/

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    /*fun getThisWeekSurveyeeList() {
        CoroutineScope(Dispatchers.IO).launch {
            val surveyeeListFromDb = surveyeeScreenUseCase.getSurveyeeListUseCase.invoke(0, "")
            surveyeeListFromDb.filter { it.movedToThisWeek }.forEach { surveyeeEntity ->
                val surveyeeState = SurveyeeCardState(
                    surveyeeDetails = surveyeeEntity,
                    imagePath = surveyeeEntity.crpImageName,
                    subtitle = surveyeeEntity.dadaName,
                    address = getSurveyeeAddress(surveyeeEntity),
                    surveyState = SurveyState.getStatusFromOrdinal(surveyeeEntity.surveyStatus)
                )
                if (!_thisWeekSurveyeeListState.value.map { it.surveyeeDetails.didiId }.contains(surveyeeEntity.didiId))
                    _thisWeekSurveyeeListState.value.add(surveyeeState)
            }
            _thisWeekFilteredSurveyeeListState.value = _thisWeekSurveyeeListState.value
        }

    }*/


    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }

            is SearchEvent.FilterList -> {
                filterList(event.fromScreen)
            }

            is SurveyeeListEvents.CancelAllSelection -> {
                if (!event.isFilterApplied) {
                    _filteredSurveyeeListState.value = _filteredSurveyeeListState.value.also {
                        it.forEach { surveyeeCardState ->
                            surveyeeCardState.isChecked = mutableStateOf(false)
                        }
                    }
                }
            }

            is SurveyeeListEvents.MoveDidisThisWeek -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (event.didiIdList.isNotEmpty()) {
                        surveyeeScreenUseCase.moveSurveyeeToThisWeek.moveSurveyeesToThisWeek(
                            event.didiIdList,
                            event.moveDidisToNextWeek
                        )
                        showMoveDidisBanner.value = true
//                        getThisWeekSurveyeeList()
                    }
                }
            }
            is SurveyeeListEvents.MoveDidiToThisWeek -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (event.didiId != -1) {
                        surveyeeScreenUseCase.moveSurveyeeToThisWeek.moveSurveyeeToThisWeek(
                            event.didiId,
                            event.moveDidisToNextWeek
                        )
//                        getThisWeekSurveyeeList()
                    }
                }
            }

            is SurveyeeListEvents.UpdateActivityStatus -> {
                CoroutineScope(Dispatchers.IO).launch {

                }
            }

            is SurveyeeListEvents.UpdateActivityAllTask -> {
                CoroutineScope(Dispatchers.IO).launch {
                    surveyeeScreenUseCase.getActivityStateFromDBUseCase.updateActivityAllTaskStatus(
                        event.activityId,
                        event.isAllTask
                    )
                }
            }

            is EventWriterEvents.UpdateActivityStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    surveyeeScreenUseCase.updateActivityStatusUseCase.invoke(
                        missionId = event.missionId,
                        activityId = event.activityId,
                        status = event.status
                    )
                    val updateTaskStatusEvent =
                        eventWriterHelperImpl.createActivityStatusUpdateEvent(
                            missionId = event.missionId,
                            activityId = event.activityId,
                            status = event.status
                        )
                    surveyeeScreenUseCase.eventsWriterUseCase.invoke(
                        events = updateTaskStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }
            is ApiStatusEvent.showApiStatus -> {
                if (event.errorCode == 200) {
                    init(missionId, activityName, activityId)
                    showCustomToast(
                        BaselineCore.getAppContext(), BaselineCore.getAppContext().getString(
                        R.string.fetched_successfully))
                } else {
                    showCustomToast(
                        BaselineCore.getAppContext(),
                        BaselineCore.getAppContext().getString(R.string.refresh_failed_please_try_again)
                    )
                }
            }

        }
    }

    override fun performSearchQuery(queryTerm: String, isFilterApplied: Boolean, fromScreen: String) {
        if (fromScreen == DIDI_LIST) {
            if (!isFilterApplied) {
                _filteredSurveyeeListState.value = if (queryTerm.isNotEmpty()) {
                    val filteredList = ArrayList<SurveyeeCardState>()
                    surveyeeListState.value.forEach { surveyeeCardState ->
                        if (surveyeeCardState.surveyeeDetails.didiName.lowercase()
                                .contains(queryTerm.lowercase())
                        ) {
                            filteredList.add(surveyeeCardState)
                        }
                    }
                    filteredList
                } else {
                    _surveyeeListState.value
                }
            } else {
                if (queryTerm.isNotEmpty()) {
                    val mFilterMap = mutableMapOf<String, MutableList<SurveyeeCardState>>()
                    tolaMapList.keys.forEach { key ->
                        val mSurveyeeCardState = ArrayList<SurveyeeCardState>()
                        tolaMapList[key]?.forEach { surveyeeCardState ->
                            if (surveyeeCardState.surveyeeDetails.didiName.lowercase()
                                    .contains(queryTerm.lowercase())
                                || surveyeeCardState.surveyeeDetails.dadaName.lowercase()
                                    .contains(queryTerm.lowercase())
                                || surveyeeCardState.surveyeeDetails.houseNo.lowercase()
                                    .contains(queryTerm.lowercase())
                            ) {
                                mSurveyeeCardState.add(surveyeeCardState)
                            }
                        }
                        if (mSurveyeeCardState.isNotEmpty())
                            mFilterMap[key] = mSurveyeeCardState
                    }
                    _filteredTolaMapSurveyeeListState.value = mFilterMap
                } else {
                    _filteredTolaMapSurveyeeListState.value = tolaMapSurveyeeListState.value
                }
            }
        } else {
            if (!isFilterApplied) {
                _filteredSurveyeeListState.value = if (queryTerm.isNotEmpty()) {
                    val filteredList = ArrayList<SurveyeeCardState>()
                    surveyeeListState.value.forEach { surveyeeCardState ->
                        if (surveyeeCardState.surveyeeDetails.didiName.lowercase()
                                .contains(queryTerm.lowercase())
                            || surveyeeCardState.surveyeeDetails.dadaName.lowercase()
                                .contains(queryTerm.lowercase())
                            || surveyeeCardState.surveyeeDetails.houseNo.lowercase()
                                .contains(queryTerm.lowercase())
                        ) {
                            filteredList.add(surveyeeCardState)
                        }
                    }
                    filteredList
                } else {
                    _surveyeeListState.value
                }
            } else {
                if (queryTerm.isNotEmpty()) {
                    val mFilterMap = mutableMapOf<String, MutableList<SurveyeeCardState>>()
                    tolaMapList.keys.forEach { key ->
                        val mSurveyeeCardState = ArrayList<SurveyeeCardState>()
                        tolaMapList[key]?.forEach { surveyeeCardState ->
                            if (surveyeeCardState.surveyeeDetails.didiName.lowercase()
                                    .contains(queryTerm.lowercase())
                            ) {
                                mSurveyeeCardState.add(surveyeeCardState)
                            }
                        }
                        if (mSurveyeeCardState.isNotEmpty())
                            mFilterMap[key] = mSurveyeeCardState
                    }
                    _filteredTolaMapSurveyeeListState.value = mFilterMap
                } else {
                    _filteredTolaMapSurveyeeListState.value = tolaMapSurveyeeListState.value
                }
            }
        }

    }

    override fun filterList(fromScreen: String) {
        if (fromScreen == ALL_TAB) {
            val map = mutableMapOf<String, MutableList<SurveyeeCardState>>()
            surveyeeListState.value.forEachIndexed { index, surveyeeCardState ->
                if (!surveyeeCardState.surveyeeDetails.cohortName.equals(NO_TOLA_TITLE, true)) {
                    if (map.contains(surveyeeCardState.surveyeeDetails.cohortName)) {
                        map[surveyeeCardState.surveyeeDetails.cohortName]?.add(surveyeeCardState)
                    } else {
                        map[surveyeeCardState.surveyeeDetails.cohortName] =
                            mutableListOf(surveyeeCardState)
                    }
                } else {
                    if (map.contains(surveyeeCardState.surveyeeDetails.villageName)) {
                        map[surveyeeCardState.surveyeeDetails.villageName]?.add(surveyeeCardState)
                    } else {
                        map[surveyeeCardState.surveyeeDetails.villageName] =
                            mutableListOf(surveyeeCardState)
                    }
                }
            }
            tolaMapList = map
            _tolaMapSurveyeeListState.value = map
            _filteredTolaMapSurveyeeListState.value = map
        } else {
            val map = mutableMapOf<String, MutableList<SurveyeeCardState>>()
            thisWeekSurveyeeListState.value.forEachIndexed { index, thisWeekSurveyeeCardState ->
                if (map.contains(thisWeekSurveyeeCardState.surveyeeDetails.cohortName)) {
                    map[thisWeekSurveyeeCardState.surveyeeDetails.cohortName]?.add(thisWeekSurveyeeCardState)
                } else {
                    map[thisWeekSurveyeeCardState.surveyeeDetails.cohortName] = mutableListOf(thisWeekSurveyeeCardState)
                }
            }
            tolaMapList = map
            _thisWeekTolaMapSurveyeeListState.value = map
        }
    }


    private fun getSurveyeeAddress(surveyeeEntity: SurveyeeEntity): String {
        return if (!surveyeeEntity.cohortName.equals(NO_TOLA_TITLE, true))
            surveyeeEntity.houseNo + ", " + surveyeeEntity.cohortName
        else
            surveyeeEntity.houseNo + ", " + surveyeeEntity.villageName
    }

    fun allTaskDone(): Boolean {
        var isFlag = false
        filteredSurveyeeListState.value.forEach { task ->
            isFlag = task.surveyState.ordinal == 2
        }
        return isFlag
    }

    fun refreshData() {
        refreshData(fetchDataUseCase)
    }


}
