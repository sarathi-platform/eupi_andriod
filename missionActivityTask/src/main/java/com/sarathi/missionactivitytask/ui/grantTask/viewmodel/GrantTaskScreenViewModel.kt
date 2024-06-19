package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.util.constants.ComponentEnum
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardModel
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
class GrantTaskScreenViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val grantConfigUseCase: GrantConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase
) : BaseViewModel() {
    private var missionId = 0
    private var activityId = 0
    var activityConfigUiModel: ActivityConfigUiModel? = null
    private val _taskList =
        mutableStateOf<HashMap<Int, HashMap<String, GrantTaskCardModel>>>(hashMapOf())
    private val taskList: State<HashMap<Int, HashMap<String, GrantTaskCardModel>>> get() = _taskList
    private val _filterList =
        mutableStateOf<HashMap<Int, HashMap<String, GrantTaskCardModel>>>(hashMapOf())
    val filterList: State<HashMap<Int, HashMap<String, GrantTaskCardModel>>> get() = _filterList
    val searchLabel = mutableStateOf<String>(BLANK_STRING)
    val isButtonEnable = mutableStateOf<Boolean>(false)
    var isDisbursement: Boolean = false
    var isGroupByEnable = mutableStateOf(false)
    var isFilerEnable = mutableStateOf(false)
    var isActivityCompleted = mutableStateOf(false)
    var isGenerateFormButtonEnable = mutableStateOf(false)

    var filterTaskMap by mutableStateOf(mapOf<String?, List<MutableMap.MutableEntry<Int, HashMap<String, GrantTaskCardModel>>>>())


    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initTaskScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }
        }
    }

    private fun initTaskScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val taskUiModel =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            getSurveyDetail()
            isActivityCompleted()
            isGenerateFormButtonEnable()
            taskUiModel.forEachIndexed { index, it ->
                if (index == 0) {
                    searchLabel.value = getUiComponentValues(
                        taskId = it.taskId,
                        taskStatus = it.status.toString(),
                        subjectId = it.subjectId,
                        formGeneratedCount = it.formGeneratedCount,
                        componentType = ComponentEnum.Search.name
                    )[GrantTaskCardSlots.GRANT_SEARCH_LABEL.name]?.value
                        ?: BLANK_STRING

                    if ((getUiComponentValues(
                            taskId = it.taskId,
                            taskStatus = it.status.toString(),
                            subjectId = it.subjectId,
                            formGeneratedCount = it.formGeneratedCount,
                            componentType = ComponentEnum.Card.name
                        )[GrantTaskCardSlots.GRANT_GROUP_BY.name]?.value
                            ?: BLANK_STRING).isNotBlank()
                    ) {
                        isFilerEnable.value = true
                    }
                }
                _taskList.value[it.taskId] =
                    getUiComponentValues(
                        taskId = it.taskId,
                        taskStatus = it.status.toString(),
                        subjectId = it.subjectId,
                        formGeneratedCount = it.formGeneratedCount,
                        componentType = ComponentEnum.Card.name
                    )
            }
            getGrantConfig()

            _filterList.value = _taskList.value

            filterTaskMap =
                _taskList.value.entries.groupBy { it.value[GrantTaskCardSlots.GRANT_GROUP_BY.name]?.value }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    private suspend fun getUiComponentValues(
        taskId: Int,
        taskStatus: String,
        formGeneratedCount: Int = 0,
        subjectId: Int,
        componentType: String
    ): HashMap<String, GrantTaskCardModel> {
        val cardAttributesWithValue = HashMap<String, GrantTaskCardModel>()
        cardAttributesWithValue[GrantTaskCardSlots.GRANT_TASK_STATUS.name] =
            GrantTaskCardModel(value = taskStatus, label = BLANK_STRING, icon = null)
        cardAttributesWithValue[GrantTaskCardSlots.GRANT_TASK_FORM_GENERATED_COUNT.name] =
            GrantTaskCardModel(
                value = formGeneratedCount.toString(),
                label = BLANK_STRING,
                icon = null
            )
        val activityConfig = getActivityUiConfigUseCase.getActivityUiConfig(
            missionId = missionId, activityId = activityId
        )
        val cardConfig = activityConfig.filter { it.componentType == componentType }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> getGrantTaskCardModel(
                    value = cardAttribute.value,
                    activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getGrantTaskCardModel(
                    value = getTaskAttributeValue(
                        cardAttribute.value,
                        taskId
                    ), activityUiConfig = cardAttribute
                )

                UiConfigAttributeType.TAG.name -> getGrantTaskCardModel(
                    activityUiConfig = cardAttribute, value = surveyAnswerUseCase.getAnswerForTag(
                        taskId,
                        subjectId,
                        getTaskAttributeValue(
                            cardAttribute.value, taskId
                        )
                    )
                )


                else -> {
                    getGrantTaskCardModel(activityUiConfig = cardAttribute, BLANK_STRING)
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
    }

    suspend fun getSurveyDetail() {
        activityConfigUiModel = getActivityConfigUseCase.getActivityUiConfig(activityId)
    }

    private fun performSearchQuery(
        queryTerm: String, isFilterApplied: Boolean, fromScreen: String
    ) {
        val filteredList = HashMap<Int, HashMap<String, GrantTaskCardModel>>()
        if (queryTerm.isNotEmpty()) {
            taskList.value.entries.forEach { task ->
                if (task.value[GrantTaskCardSlots.GRANT_SEARCH_ON.name]?.value?.lowercase()
                        ?.contains(queryTerm.lowercase()) == true
                ) {
                    filteredList[task.key] = task.value
                }
            }
        } else {
            filteredList.putAll(taskList.value)
        }
        _filterList.value = filteredList
    }


    fun checkButtonValidation() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            isButtonEnable.value = getTaskUseCase.isAllActivityCompleted(
                missionId = missionId,
                activityId = activityId
            ) && !isActivityCompleted.value
        }
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

    suspend fun getGrantConfig() {
        activityConfigUiModel?.activityConfigId?.let {
            val grantConfigs = grantConfigUseCase.getGrantConfig(it)
            isDisbursement = grantConfigs.isNotEmpty()

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
            } else {
                taskStatusUseCase.markTaskInProgress(taskId = taskId)
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


        }
    }

    private fun isActivityCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isActivityCompleted.value = getActivityUseCase.isAllActivityCompleted(
                missionId = missionId ?: 0,
                activityId = activityId ?: 0
            )
            checkButtonValidation()
        }


    }

    private fun getGrantTaskCardModel(
        activityUiConfig: UiConfigModel,
        value: String
    ): GrantTaskCardModel {
        return GrantTaskCardModel(
            label = activityUiConfig.label,
            value = value,
            icon = getFilePathUri(activityUiConfig.icon ?: BLANK_STRING)
        )

    }

    private suspend fun isGenerateFormButtonEnable() {
        isGenerateFormButtonEnable.value =
            formUseCase.getNonGeneratedFormSummaryData(activityId).isNotEmpty()

    }

}