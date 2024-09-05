package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreObserverManager
import com.nudge.core.FilterCore
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.commonUi.CustomProgressState
import com.nudge.core.ui.commonUi.DEFAULT_PROGRESS_VALUE
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.ALL
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.util.constants.ComponentEnum
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import com.sarathi.missionactivitytask.utils.event.TaskScreenEvent
import com.sarathi.missionactivitytask.utils.toHashMap
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
open class TaskScreenViewModel @Inject constructor(
    val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
) : BaseViewModel() {
    var missionId = 0
    var activityId = 0
    var activityType: String? = null
    var activityConfigUiModel: ActivityConfigUiModel? = null
    var activityConfigUiModelWithoutSurvey: ActivityConfigEntity? = null
    private val _taskList =
        mutableStateOf<HashMap<Int, HashMap<String, TaskCardModel>>>(hashMapOf())
    val taskList: State<HashMap<Int, HashMap<String, TaskCardModel>>> get() = _taskList
    private val _filterList =
        mutableStateOf<HashMap<Int, HashMap<String, TaskCardModel>>>(hashMapOf())
    val filterList: State<HashMap<Int, HashMap<String, TaskCardModel>>> get() = _filterList
    val searchLabel = mutableStateOf<String>(BLANK_STRING)

    /**
     * Handles the state of Activity Completion button
     * */
    val isButtonEnable = mutableStateOf<Boolean>(false)

    /**
     * Handles the visibility of groupBy button
     * */
    var isGroupByEnable = mutableStateOf(false)

    /**
     * Handles the state of groupBy button
     * */
    var isGroupingApplied = mutableStateOf(false)

    /**
     * Handles the visibility of filter button
     * */
    var isFilterEnabled = mutableStateOf(false)

    /**
     * Handles the state of filter button
     * */

    var isFilterApplied = mutableStateOf(false)

    var isActivityCompleted = mutableStateOf(false)

    /**
     * Handles the visibility of progress bar
     * */
    var isProgressEnable = mutableStateOf(true)

    var matId = mutableStateOf<Int>(0)
    var contentCategory = mutableStateOf<Int>(0)
    var filterTaskMap by mutableStateOf(mapOf<String?, List<MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>>>())
    var taskUiModel: List<TaskUiModel>? = null
    var showDialog = mutableStateOf<Boolean>(false)

    private val _filterByList: SnapshotStateList<String?> = mutableStateListOf()
    val filterByList: SnapshotStateList<String?> get() = _filterByList

    private val _filterByValueKey: MutableState<String> = mutableStateOf(ALL)
    val filterByValueKey: State<String> get() = _filterByValueKey

    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, BLANK_STRING)

    private suspend fun <T> updateValueInMainThread(mutableState: MutableState<T>, newValue: T) {
        withContext(Dispatchers.Main) {
            mutableState.value = newValue
        }
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitTaskScreenState -> {
                initTaskScreen(event.taskList)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(
                    queryTerm = event.searchTerm,
                    isGroupingApplied = event.isGroupingApplied,
                    isFilterApplied = event.isFilterApplied
                )
            }

            is TaskScreenEvent.OnGroupBySelected -> {
                isGroupingApplied.value = !isGroupingApplied.value
            }

            is TaskScreenEvent.OnFilterSelected -> {
                _filterByValueKey.value = filterByList[event.index].value()
                FilterCore.setFilterValueForActivity(activityId, event.index)
                onFilterSelected()
            }
        }
    }

    private fun onFilterSelected() {
        if (filterByList.indexOf(filterByValueKey.value) != FilterCore.DEFAULT_FILTER_INDEX_VALUE) {
            isFilterApplied.value = true
            updateListForSelectedFilter()
        } else {
            isFilterApplied.value = false

            updateListForAllFilter()
        }
    }

    private fun updateListForAllFilter() {
        filterTaskMap =
            taskList.value.entries
                .groupBy { it.value[TaskCardSlots.GROUP_BY.name]?.value }
        _filterList.value = taskList.value
    }

    private fun updateListForSelectedFilter() {

        val sortedList = taskList.value

        val tempFilterTaskMap = sortedList
            .filter {
                it.value[TaskCardSlots.FILTER_BY.name]?.value.equals(
                    filterByValueKey.value, ignoreCase = true
                )
            }.toHashMap()
        filterTaskMap =
            tempFilterTaskMap.entries.groupBy { it.value[TaskCardSlots.GROUP_BY.name]?.value }

        val tempFilterList = sortedList.filter {
            it.value[TaskCardSlots.FILTER_BY.name]?.value.equals(
                filterByValueKey.value, ignoreCase = true
            )
        }
        _filterList.value =
            tempFilterList.toHashMap()
    }

    fun initTaskScreen(taskList: List<TaskUiModel>?) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            activityConfigUiModelWithoutSurvey =
                getActivityUiConfigUseCase.getActivityConfig(activityId, missionId)
            onEvent(LoaderEvent.UpdateLoaderState(true))

            taskUiModel = if (taskList.isNullOrEmpty()) getTaskUseCase.getActiveTasks(
                missionId = missionId,
                activityId = activityId
            ) else taskList
            isContentScreenEmpty()
            getSurveyDetail()
            isActivityCompleted()
            val activityConfig = getActivityUiConfigUseCase.getActivityUiConfig(
                missionId = missionId, activityId = activityId
            )

            taskUiModel?.forEachIndexed { index, it ->
                val uiComponent = getUiComponentValues(
                    taskId = it.taskId,
                    taskStatus = it.status.toString(),
                    isTaskSecondaryStatusEnable = it.isTaskSecondaryStatusEnable,
                    isNAButtonEnable = it.isNotAvailableButton,
                    subjectId = it.subjectId,
                    componentType = ComponentEnum.Card.name,
                    activityConfig
                )

                if (index == 0) {
                    val searchUiComponent = getUiComponentValues(
                        taskId = it.taskId,
                        taskStatus = it.status.toString(),
                        isTaskSecondaryStatusEnable = it.isTaskSecondaryStatusEnable,
                        isNAButtonEnable = it.isNotAvailableButton,
                        subjectId = it.subjectId,
                        componentType = ComponentEnum.Search.name,
                        activityConfig

                    )
                    searchLabel.value =
                        searchUiComponent[TaskCardSlots.SEARCH_LABEL.name]?.value
                            ?: BLANK_STRING

                    if ((uiComponent[TaskCardSlots.GROUP_BY.name]?.value
                            ?: BLANK_STRING).isNotBlank()
                    ) {
                        isGroupByEnable.value = true
                        isGroupingApplied.value = true
                    }
                    if ((uiComponent[TaskCardSlots.FILTER_BY.name]?.value
                            ?: BLANK_STRING).isNotBlank()
                    ) {
                        isFilterEnabled.value = true
                    }
                    val progressUiComponent = getUiComponentValues(
                        taskId = it.taskId,
                        taskStatus = it.status.toString(),
                        isTaskSecondaryStatusEnable = it.isTaskSecondaryStatusEnable,
                        isNAButtonEnable = it.isNotAvailableButton,
                        subjectId = it.subjectId,
                        componentType = ComponentEnum.Progress.name,
                        activityConfig = activityConfig
                    )

                    isProgressEnable.value =
                        progressUiComponent[TaskCardSlots.TASK_PROGRESS.name]?.value != null
                }

                if (uiComponent[TaskCardSlots.TASK_TITLE.name]?.value?.isNotEmpty() == true)
                    _taskList.value[it.taskId] = uiComponent

            }

            var _filterListt = _taskList.value
            updateValueInMainThread(
                _filterList,
                _filterListt
            )

            filterTaskMap =
                _taskList.value.entries.groupBy { it.value[TaskCardSlots.GROUP_BY.name]?.value }

            updateListForAllFilter()

            if (isFilterEnabled.value) {

                createFilterByList()
                updateFilterForActivity(activityId)
            }

            updateProgress()

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun updateFilterForActivity(activityId: Int) {
        val filterValueFromCore = FilterCore.getFilterValueForActivity(activityId)
        val defaultFilterIndex = filterByList.indexOf(filterByValueKey.value).value(0)
        if (filterValueFromCore != defaultFilterIndex) {
            isFilterApplied.value = true
            _filterByValueKey.value = filterByList[filterValueFromCore].value()
        }
        onEvent(TaskScreenEvent.OnFilterSelected(filterValueFromCore))

    }

    private fun createFilterByList() {
        val context = CoreAppDetails.getContext()
        val allOption = context?.getString(R.string.all_filter_text)
        _filterByList.clear()
        _filterByList.add(allOption)
        _taskList.value.entries.map { it.value[TaskCardSlots.FILTER_BY.name]?.value }.let {
            _filterByList.addAll(it.distinct())
        }
    }

    private fun updateProgress() {
        val completedCount = (taskList.value.entries.filter {
            it.value[TaskCardSlots.TASK_STATUS.name]?.value == SurveyStatusEnum.NOT_AVAILABLE.name
                    || it.value[TaskCardSlots.TASK_STATUS.name]?.value == SurveyStatusEnum.COMPLETED.name
        }.size)
        val totalCount = (taskList.value.entries.size)

        progressState.updateProgress(completedCount.toFloat() / totalCount.toFloat())
        progressState.updateProgressText("$completedCount/$totalCount")

    }


    private suspend fun getUiComponentValues(
        taskId: Int,
        taskStatus: String,
        isTaskSecondaryStatusEnable: Boolean?,
        isNAButtonEnable: Boolean?,
        subjectId: Int,
        componentType: String,
        activityConfig: List<UiConfigModel>
    ): HashMap<String, TaskCardModel> {
        val cardAttributesWithValue = HashMap<String, TaskCardModel>()
        cardAttributesWithValue[TaskCardSlots.TASK_STATUS.name] =
            TaskCardModel(value = taskStatus, label = BLANK_STRING, icon = null)
        cardAttributesWithValue[TaskCardSlots.TASK_SECOND_STATUS_AVAILABLE.name] =
            TaskCardModel(
                value = isTaskSecondaryStatusEnable.toString(),
                label = BLANK_STRING,
                icon = null
            )
        cardAttributesWithValue[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name] =
            TaskCardModel(
                value = isNAButtonEnable.toString(),
                label = BLANK_STRING,
                icon = null
            )

        val cardConfig = activityConfig.filter { it.componentType == componentType }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> getTaskCardModel(
                    value = cardAttribute.value,
                    activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskCardModel(
                    value = getTaskAttributeValue(
                        cardAttribute.value,
                        taskId
                    ), activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.TAG.name -> getTaskCardModel(
                    activityUiConfig = cardAttribute, value = surveyAnswerUseCase.getAnswerForTag(
                        taskId,
                        subjectId,
                        getTaskAttributeValue(
                            cardAttribute.value, taskId
                        )
                    )
                )


                else -> {
                    getTaskCardModel(activityUiConfig = cardAttribute, BLANK_STRING)
                }
            }


        }

        return cardAttributesWithValue
    }

    private suspend fun getTaskAttributeValue(key: String, taskId: Int): String {

        return getTaskUseCase.getSubjectAttributes(taskId).find { it.key == key }?.value
            ?: BLANK_STRING


    }

    fun setMissionActivityId(missionId: Int, activityId: Int) {
        this.missionId = missionId
        this.activityId = activityId
        getActivityType(missionId, activityId)
    }

    private fun getActivityType(missionId: Int, activityId: Int) {
        ioViewModelScope {
            activityType = getActivityUseCase.getTypeForActivity(missionId, activityId)
        }

    }

    suspend fun getSurveyDetail() {
        activityConfigUiModel = getActivityConfigUseCase.getActivityUiConfig(activityId)
    }

    private fun performSearchQuery(
        queryTerm: String, isGroupingApplied: Boolean, isFilterApplied: Boolean
    ) {

        val sortedList = taskList.value

        val taskListForAppliedFilter = if (isFilterApplied) {
            val tempFilterList =
                sortedList.filter { it.value[TaskCardSlots.FILTER_BY.name]?.value == filterByValueKey.value }
            tempFilterList.toHashMap()
        } else {
            sortedList
        }

        val finalFilteredList = HashMap<Int, HashMap<String, TaskCardModel>>()
        if (queryTerm.isNotEmpty()) {
            taskListForAppliedFilter.entries.forEach { task ->
                if (task.value[TaskCardSlots.SEARCH_ON.name]?.value?.lowercase()
                        ?.contains(queryTerm.lowercase()) == true || task.value[TaskCardSlots.GROUP_BY.name]?.value?.lowercase()
                        ?.contains(queryTerm.lowercase()) == true
                ) {
                    finalFilteredList[task.key] = task.value
                }
            }
        } else {
            finalFilteredList.putAll(taskListForAppliedFilter)
        }
        if (isGroupingApplied) {
            filterTaskMap =
                finalFilteredList.entries.groupBy { it.value[TaskCardSlots.GROUP_BY.name]?.value }
        } else {
            _filterList.value = finalFilteredList
        }
    }

    suspend fun checkButtonValidation() {
        var isButtonEnablee = getTaskUseCase.isAllActivityCompleted(
            missionId = missionId,
            activityId = activityId
        ) && !isActivityCompleted.value
        updateValueInMainThread(isButtonEnable, isButtonEnablee)
    }

    fun markActivityCompleteStatus() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            taskStatusUseCase.markActivityCompleted(
                missionId = missionId,
                activityId = activityId
            )
            eventWriterUseCase.updateActivityStatus(
                missionId = missionId,
                activityId = activityId, surveyName = "CSG"
            )
        }
    }

    fun getFilePathUri(filePath: String): Uri? {
        return fetchContentUseCase.getFilePathUri(filePath)
    }
    fun updateTaskAvailableStatus(
        taskId: Int,
        status: String,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            if (status == SurveyStatusEnum.NOT_AVAILABLE.name) {
                taskStatusUseCase.markTaskNotAvailable(taskId = taskId)
            }
            taskStatusUseCase.markActivityInProgress(missionId, activityId)
            taskStatusUseCase.markMissionInProgress(missionId)
            eventWriterUseCase.markMATStatus(
                missionId = missionId,
                activityId = activityId,
                taskId = taskId,
                subjectType = activityConfigUiModel?.subject ?: BLANK_STRING,
                surveyName = "CSG"
            )
            getTaskUseCase.updateTaskStatus(
                taskId = taskId,
                status = status
            )
            updateProgress()
        }
    }

    fun isActivityCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isActivityCompleted.value = getActivityUseCase.isAllActivityCompleted(
                missionId = missionId,
                activityId = activityId
            )
            checkButtonValidation()
        }
    }

    private suspend fun isContentScreenEmpty() {
        val isContentEmpty = fetchContentUseCase.getContentCount(
            matId = activityId,
            contentCategory = ContentCategoryEnum.ACTIVITY.ordinal
        ) == 0
        if (isContentEmpty) {
            matId.value = missionId
            contentCategory.value = ContentCategoryEnum.MISSION.ordinal
        } else {
            matId.value = activityId
            contentCategory.value = ContentCategoryEnum.ACTIVITY.ordinal
        }

    }


    private fun getTaskCardModel(
        activityUiConfig: UiConfigModel,
        value: String
    ): TaskCardModel {
        return TaskCardModel(
            label = activityUiConfig.label,
            value = value,
            icon = getFilePathUri(activityUiConfig.icon ?: BLANK_STRING)
        )

    }

    override fun refreshData() {
        super.refreshData()
        loadAllData(isRefresh = true)
    }

    private fun loadAllData(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchAllDataUseCase.invoke({ isSuccess, successMsg ->
                // Temp method to be removed after baseline is migrated to Grant flow.
                updateStatusForBaselineMission() { success ->
                    CoreLogger.i(
                        tag = "MissionScreenViewMode",
                        msg = "updateStatusForBaselineMission: success: $success"
                    )
                    initTaskScreen(taskUiModel) // Move this out of the lambda block once the above method is removed
                }
            }, isRefresh = isRefresh)
        }
    }

    // Temp method to be removed after baseline is migrated to Grant flow.
    private fun updateStatusForBaselineMission(onSuccess: (isSuccess: Boolean) -> Unit) {
        CoreObserverManager.notifyCoreObserversUpdateMissionActivityStatusOnGrantInit() {
            onSuccess(it)
        }
    }

}