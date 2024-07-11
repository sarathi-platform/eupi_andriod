package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.SANCTIONED_AMOUNT_EQUAL_DISBURSED_FORM_E_GENERATED
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GrantTaskScreenViewModel @Inject constructor(
    getTaskUseCase: GetTaskUseCase,
    surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    getActivityConfigUseCase: GetActivityConfigUseCase,
    fetchContentUseCase: FetchContentUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    eventWriterUseCase: MATStatusEventWriterUseCase,
    getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase,
    private val formUiConfigUseCase: GetFormUiConfigUseCase,
    fetchAllDataUseCase: FetchAllDataUseCase,
) : TaskScreenViewModel(
    getTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase
) {
    var taskUiList = mutableStateOf<List<TaskUiModel>>(emptyList())
    var isGenerateFormButtonEnable = mutableStateOf(false)
    var isGenerateFormButtonVisible = mutableStateOf(false)

    private suspend fun <T> updateValueInMainThread(mutableState: MutableState<T>, newValue: T) {
        withContext(Dispatchers.Main) {
            mutableState.value = newValue
        }
    }

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitGrantTaskScreenState -> {
                initGrantTaskScreen(event.missionId, event.activityId)
            }

        }
    }

    private fun initGrantTaskScreen(missionId: Int, activityId: Int) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskUiList.value =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            isGenerateFormButtonEnable(missionId, activityId)
            isContentScreenEmpty()
            getSurveyDetail()
            isActivityCompleted()
            isGenerateFormButtonEnable()
            taskUiModel.forEachIndexed { index, it ->

                val uiComponent = getUiComponentValues(
                    taskId = it.taskId,
                    taskStatus = it.status.toString(),
                    subjectId = it.subjectId,
                    formGeneratedCount = it.formGeneratedCount,
                    componentType = ComponentEnum.Card.name
                )
                if (index == 0) {
                    val searchUiComponent = getUiComponentValues(
                        taskId = it.taskId,
                        taskStatus = it.status.toString(),
                        subjectId = it.subjectId,
                        formGeneratedCount = it.formGeneratedCount,
                        componentType = ComponentEnum.Search.name
                    )
                    searchLabel.value =
                        searchUiComponent[GrantTaskCardSlots.GRANT_SEARCH_LABEL.name]?.value
                            ?: BLANK_STRING

                    if ((uiComponent[GrantTaskCardSlots.GRANT_GROUP_BY.name]?.value
                            ?: BLANK_STRING).isNotBlank()
                    ) {
                        isFilerEnable.value = true
                    }
                }
                _taskList.value[it.taskId] = uiComponent

            }
            getGrantConfig()

            var _filterListt = _taskList.value
            updateValueInMainThread(_filterList, _filterListt)

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
        if (isFilterApplied) {
            filterTaskMap =
                filteredList.entries.groupBy { it.value[GrantTaskCardSlots.GRANT_GROUP_BY.name]?.value }
        } else {
            _filterList.value = filteredList
        }
    }

    private suspend fun checkButtonValidation() {
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

    suspend fun getGrantConfig() {
        activityConfigUiModel?.activityConfigId?.let {
            val grantConfigs = grantConfigUseCase.getGrantConfig(it)
            isDisbursement = grantConfigs.isNotEmpty()

        }
    }


    private suspend fun isGenerateFormButtonEnable(missionId: Int, activityId: Int) {
        isGenerateFormButtonVisible.value =
            formUiConfigUseCase.getFormUiConfig(missionId = missionId, activityId = activityId)
                .isNotEmpty()
        if (isGenerateFormButtonVisible.value) {
            isGenerateFormButtonEnable.value =
                formUseCase.getNonGeneratedFormSummaryData(activityId)
                    .isNotEmpty() && !isActivityCompleted.value

        }
    }


    fun getTaskListOfDisburesementAmountEqualSanctionedAmount(): String {
        val taskListSanctionedEqualDisbursed = ArrayList<String>()
        if (activityConfigUiModel?.taskCompletion == SANCTIONED_AMOUNT_EQUAL_DISBURSED_FORM_E_GENERATED) {
            taskList.value.entries.forEach { task ->

                if (task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_5.name]?.value?.toInt() == task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]?.value?.toInt() && (task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value != SurveyStatusEnum.COMPLETED.name || task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value != SurveyStatusEnum.NOT_AVAILABLE.name)) {
                    taskListSanctionedEqualDisbursed.add(task.key.toString())
                }
            }
        } else {
            return BLANK_STRING
        }
        return taskListSanctionedEqualDisbursed.joinToString(DELEGATE_COMM)
    }


}