package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityUiConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardSlots
import com.sarathi.missionactivitytask.ui.grantTask.model.UiConfigAttributeType
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
    private val getActivityConfigUseCase: GetActivityConfigUseCase
) : BaseViewModel() {
    private var missionId = 0
    private var activityId = 0
    var activityConfigUiModel: ActivityConfigUiModel? = null
    private val _taskList = mutableStateOf<HashMap<Int, HashMap<String, String>>>(hashMapOf())
    private val taskList: State<HashMap<Int, HashMap<String, String>>> get() = _taskList
    private val _filterList = mutableStateOf<HashMap<Int, HashMap<String, String>>>(hashMapOf())
    val filterList: State<HashMap<Int, HashMap<String, String>>> get() = _filterList
    val searchLabel = mutableStateOf<String>(BLANK_STRING)
    val isButtonEnable = mutableStateOf<Boolean>(false)

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
            checkButtonValidation()
            taskUiModel.forEachIndexed { index, it ->
                if (index == 0) {
                    searchLabel.value = getUiComponentValues(
                        it.taskId,
                        it.status.toString(),
                        it.subjectId,
                        componentType = "Search"
                    )[GrantTaskCardSlots.GRANT_SEARCH_LABEL.name]
                        ?: BLANK_STRING
                }
                _taskList.value[it.taskId] =
                    getUiComponentValues(
                        it.taskId,
                        it.status.toString(),
                        it.subjectId,
                        componentType = "Card"
                    )
            }

            _filterList.value.putAll(_taskList.value)
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    private suspend fun getUiComponentValues(
        taskId: Int,
        taskStatus: String,
        subjectId: Int,
        componentType: String
    ): HashMap<String, String> {
        val cardAttributesWithValue = HashMap<String, String>()
        cardAttributesWithValue[GrantTaskCardSlots.GRANT_TASK_STATUS.name] = taskStatus
        val activityConfig = getActivityUiConfigUseCase.getActivityUiConfig(
            missionId = missionId, activityId = activityId
        )
        val cardConfig = activityConfig.filter { it.componentType == componentType }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> cardAttribute.value
                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskAttributeValue(
                    cardAttribute.value, taskId
                )

                UiConfigAttributeType.TAG.name -> surveyAnswerUseCase.getAnswerForTag(
                    taskId,
                    subjectId,
                    getTaskAttributeValue(
                        cardAttribute.value, taskId
                    )
                )

                else -> {
                    BLANK_STRING
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
        val filteredList = HashMap<Int, HashMap<String, String>>()
        if (queryTerm.isNotEmpty()) {
            taskList.value.entries.forEach { task ->
                if (task.value[GrantTaskCardSlots.GRANT_SEARCH_ON.name]?.lowercase()
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

    private fun checkButtonValidation() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            isButtonEnable.value = getTaskUseCase.isAllActivityCompleted()
        }
    }

    fun markActivityCompleteStatus() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            getTaskUseCase.markActivityCompleteStatus(
                missionId = missionId,
                activityId = activityId
            )
        }
    }

}