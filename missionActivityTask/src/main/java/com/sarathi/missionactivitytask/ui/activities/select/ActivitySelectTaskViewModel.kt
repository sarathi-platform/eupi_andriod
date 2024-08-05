package com.sarathi.missionactivitytask.ui.activities.select

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.json
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.util.constants.ComponentEnum
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.getFilePathUri
import com.sarathi.missionactivitytask.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class ActivitySelectTaskViewModel @Inject constructor(
    val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fromEUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val coreSharedPrefs: CoreSharedPrefs
) : BaseViewModel() {
    var missionId = 0
    var activityId = 0
    var taskUiModel: List<TaskUiModel>? = null
    val searchLabel = mutableStateOf<String>(BLANK_STRING)
    val isButtonEnable = mutableStateOf<Boolean>(false)
    var isGroupByEnable = mutableStateOf(false)
    var isFilterEnable = mutableStateOf(false)
    var isActivityCompleted = mutableStateOf(false)
    var activityConfigUiModel: ActivityConfigUiModel? = null

    var matId = mutableStateOf<Int>(0)
    var contentCategory = mutableStateOf<Int>(0)
    private val _taskList =
        mutableStateOf<HashMap<Int, HashMap<String, TaskCardModel>>>(hashMapOf())
    val taskList: State<HashMap<Int, HashMap<String, TaskCardModel>>> get() = _taskList
    private val _filterList =
        mutableStateOf<HashMap<Int, HashMap<String, TaskCardModel>>>(hashMapOf())
    val filterList: State<HashMap<Int, HashMap<String, TaskCardModel>>> get() = _filterList
    var filterTaskMap by mutableStateOf(mapOf<String?, List<MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>>>())

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
        }
    }

    fun initTaskScreen(taskList: List<TaskUiModel>?) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            onEvent(LoaderEvent.UpdateLoaderState(true))
            Log.d("TAG", "initTaskScreen: $missionId :: $activityId")
            taskUiModel = if (taskList.isNullOrEmpty()) getTaskUseCase.getActiveTasks(
                missionId = missionId, activityId = activityId
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

                Log.d(
                    "TAG",
                    "initTaskScreen uiComponent: ${uiComponent.json()} :: $index : ${it.json()} "
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
                        searchUiComponent[TaskCardSlots.SEARCH_LABEL.name]?.value ?: BLANK_STRING

                    if ((uiComponent[TaskCardSlots.GROUP_BY.name]?.value
                            ?: BLANK_STRING).isNotBlank()
                    ) {
                        isGroupByEnable.value = true
                        isFilterEnable.value = true
                    }
                }
                _taskList.value[it.taskId] = uiComponent
                Log.d("TAG", "initTaskScreen_taskList 1: ${_taskList.value.json()}")

            }

            Log.d("TAG", "initTaskScreen_taskList: ${taskList?.json()}")

            var _filterListt = _taskList.value
            updateValueInMainThread(_filterList, _filterListt)

            Log.d("TAG", "initTaskScreen filterTaskMap1: ${_taskList.value.json()}")

            filterTaskMap =
                _taskList.value.entries.groupBy { it.value[TaskCardSlots.GROUP_BY.name]?.value }
            Log.d("TAG", "initTaskScreen filterTaskMap: ${filterTaskMap.json()}")
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun isContentScreenEmpty() {
        val isContentEmpty = fetchContentUseCase.getContentCount(
            matId = activityId, contentCategory = ContentCategoryEnum.ACTIVITY.ordinal
        ) == 0
        if (isContentEmpty) {
            matId.value = missionId
            contentCategory.value = ContentCategoryEnum.MISSION.ordinal
        } else {
            matId.value = activityId
            contentCategory.value = ContentCategoryEnum.ACTIVITY.ordinal
        }

    }

    suspend fun getSurveyDetail() {
        activityConfigUiModel = getActivityConfigUseCase.getActivityUiConfig(activityId)
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
        cardAttributesWithValue[TaskCardSlots.TASK_SECOND_STATUS_AVAILABLE.name] = TaskCardModel(
            value = isTaskSecondaryStatusEnable.toString(), label = BLANK_STRING, icon = null
        )
        cardAttributesWithValue[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name] = TaskCardModel(
            value = isNAButtonEnable.toString(), label = BLANK_STRING, icon = null
        )

        val cardConfig = activityConfig.filter { it.componentType == componentType }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> getTaskCardModel(
                    value = cardAttribute.value, activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskCardModel(
                    value = getTaskAttributeValue(
                        cardAttribute.value, taskId
                    ), activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.TAG.name -> getTaskCardModel(
                    activityUiConfig = cardAttribute, value = surveyAnswerUseCase.getAnswerForTag(
                        taskId, subjectId, getTaskAttributeValue(
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

    private fun getTaskCardModel(
        activityUiConfig: UiConfigModel, value: String
    ): TaskCardModel {
        return TaskCardModel(
            label = activityUiConfig.label,
            value = value,
            icon = getFilePathUri(activityUiConfig.icon ?: BLANK_STRING)
        )

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

    private suspend fun checkButtonValidation() {
        var isButtonEnablee = getTaskUseCase.isAllActivityCompleted(
            missionId = missionId,
            activityId = activityId
        ) && !isActivityCompleted.value
        updateValueInMainThread(isButtonEnable, isButtonEnablee)
    }

    private suspend fun <T> updateValueInMainThread(mutableState: MutableState<T>, newValue: T) {
        withContext(Dispatchers.Main) {
            mutableState.value = newValue
        }
    }

    fun setMissionActivityId(missionId: Int, activityId: Int) {
        this.missionId = missionId
        this.activityId = activityId
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
}