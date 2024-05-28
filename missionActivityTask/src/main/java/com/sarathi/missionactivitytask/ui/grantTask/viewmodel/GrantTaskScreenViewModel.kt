package com.sarathi.missionactivitytask.ui.grantTask.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityUiConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetTaskUseCase
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
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
) : BaseViewModel() {
    private var missionId = 0
    private var activityId = 0
    private val _taskList = mutableStateOf<HashMap<Int, HashMap<String, String>>>(hashMapOf())
    val taskList: State<HashMap<Int, HashMap<String, String>>> get() = _taskList
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
        }
    }

    private fun initTaskScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val taskUiModel =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            taskUiModel.forEach {

                _taskList.value[it.taskId] = getUiComponentValues(it.taskId)
            }

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    private suspend fun getUiComponentValues(taskId: Int): HashMap<String, String> {
        val cardAttributesWithValue = HashMap<String, String>()
        val activityConfig = getActivityUiConfigUseCase.getActivityUiConfig(
            missionId = missionId, activityId = activityId
        )
        val cardConfig = activityConfig.filter { it.componentType == "Card" }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type) {
                UiConfigAttributeType.STATIC.name -> cardAttribute.value
                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskAttributeValue(
                    cardAttribute.value, taskId
                )

                UiConfigAttributeType.TAG.name -> ""
                else -> {
                    ""
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

}