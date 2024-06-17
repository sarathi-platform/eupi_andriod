package com.sarathi.missionactivitytask.ui.disbursement_summary_screen.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.constants.ComponentEnum
import com.sarathi.dataloadingmangement.util.constants.GrantTaskFormSlots
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
class DisbursementFormSummaryScreenViewModel @Inject constructor(
    private val formUseCase: FormUseCase,
    private val getFormUiConfigUseCase: GetFormUiConfigUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
) :
    BaseViewModel() {
    private val _disbursementFormList =
        mutableStateOf<List<DisbursementFormSummaryUiModel>>(emptyList())
    val disbursementFormList: State<List<DisbursementFormSummaryUiModel>> get() = _disbursementFormList
    private val _formList =
        mutableStateOf<Map<String, List<DisbursementFormSummaryUiModel>>>(hashMapOf())
    val formList: State<Map<String, List<DisbursementFormSummaryUiModel>>> get() = _formList

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initDisbursementSummaryScreen()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initDisbursementSummaryScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _formList.value = getFormData().groupBy { it.subjectName }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun getFormData(): List<DisbursementFormSummaryUiModel> {
        val fromData = formUseCase.getFormSummaryData()
        val list = ArrayList<DisbursementFormSummaryUiModel>()
        fromData.forEach { form ->
            val _data = getFormAttributeDate(form)
            list.add(
                DisbursementFormSummaryUiModel(
                    subjectType = form.subjectType,
                    date = form.formGenerateDate,
                    noOfDidi = 1,
                    subjectName = _data[GrantTaskFormSlots.GRANT_TASK_TITLE_FORM.name]
                        ?: BLANK_STRING,
                    villageName = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_2_FORM.name]
                        ?: BLANK_STRING,
                    mode = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_4_FORM.name]
                        ?: BLANK_STRING,
                    nature = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_6_FORM.name]
                        ?: BLANK_STRING,
                    amount = _data[GrantTaskFormSlots.GRANT_TASK_SUBTITLE_5_FORM.name]
                        ?: BLANK_STRING,
                    didiImage = _data[GrantTaskFormSlots.GRANT_TASK_IMAGE_FORM.name]
                        ?: BLANK_STRING,
                )
            )
        }
        return list
    }

    private suspend fun getFormAttributeDate(form: FormEntity): HashMap<String, String> {
        return getUiComponentValues(
            taskId = form.taskid,
            subjectId = form.subjectid,
            componentType = ComponentEnum.Form.name,
            referenceId = form.localReferenceId
        )
    }

    private suspend fun getTaskAttributeValue(key: String, taskId: Int): String {
        return getTaskUseCase.getSubjectAttributes(taskId).find { it.key == key }?.value
            ?: BLANK_STRING
    }


    private suspend fun getUiComponentValues(
        taskId: Int,
        subjectId: Int,
        componentType: String,
        referenceId: String
    ): HashMap<String, String> {
        val cardAttributesWithValue = HashMap<String, String>()
        val activityConfig = getFormUiConfigUseCase.getFormUiConfig()
        val cardConfig = activityConfig.filter { it.componentType.equals(componentType, true) }
        cardConfig.forEach { cardAttribute ->
            cardAttributesWithValue[cardAttribute.key] = when (cardAttribute.type.toUpperCase()) {
                UiConfigAttributeType.STATIC.name -> cardAttribute.value
                UiConfigAttributeType.DYNAMIC.name, UiConfigAttributeType.ATTRIBUTE.name -> getTaskAttributeValue(
                    cardAttribute.value, taskId
                )

                UiConfigAttributeType.TAG.name -> surveyAnswerUseCase.getAnswerForFormTag(
                    taskId = taskId,
                    subjectId = subjectId,
                    referenceId = referenceId,
                    tagId = getTaskAttributeValue(
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

    fun getFilePathUri(filePath: String): Uri? {
        return formUseCase.getFilePathUri(filePath)
    }

}

