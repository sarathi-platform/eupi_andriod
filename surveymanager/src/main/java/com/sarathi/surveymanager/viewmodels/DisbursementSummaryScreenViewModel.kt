package com.sarathi.surveymanager.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.GrantConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyUIModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.constants.SurveyCardTag
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DisbursementSummaryScreenViewModel @Inject constructor(
    private val grantConfigUseCase: GrantConfigUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val formUseCase: FormUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
) : BaseViewModel() {
    private val _taskList =
        mutableStateOf<Map<String, List<SurveyAnswerFormSummaryUiModel>>>(hashMapOf())
    val taskList: State<Map<String, List<SurveyAnswerFormSummaryUiModel>>> get() = _taskList

    private var surveyId: Int = 0
    private var sectionId: Int = 0
    private var taskId: Int = 0
    private var subjectType: String = ""
    private var activityConfigId: Int = 0
    var showDialog = mutableStateOf(Pair<Boolean, String?>(false, BLANK_STRING))
    var grantConfigUi = mutableStateOf(GrantConfigUiModel(null, "", 0))
    val isButtonEnable = mutableStateOf<Boolean>(false)
    private var taskEntity: ActivityTaskEntity? = null
    var isActivityCompleted = mutableStateOf(false)
    var isAddDisbursementButtonEnable = mutableStateOf(true)
    private var sanctionedAmount: Int = 0
    private var totalSubmittedAmount = 0

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initData()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }


            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    //   saveSurveyAnswerUseCase.saveSurveyAnswer(event,subjectId)

                }
            }

        }
    }

    private fun initData() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _taskList.value =
                saveSurveyAnswerUseCase.getAllSaveAnswer(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    taskId = taskId
                ).groupBy { it.referenceId }
            setGrantComponentDTO()
            taskEntity = getTaskUseCase.getTask(taskId)
            isActivityCompleted()
        }
    }


    fun setPreviousScreenData(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        subjectType: String,
        activityConfigId: Int,
        sanctionedAmount: Int
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.subjectType = subjectType
        this.activityConfigId = activityConfigId
        this.sanctionedAmount = sanctionedAmount
    }

    fun createReferenceId(): String {
        return UUID.randomUUID().toString()
    }

    fun getSurveyUIModel(surveyList: List<SurveyAnswerFormSummaryUiModel>): SurveyUIModel {
        var subTitle1 = BLANK_STRING
        var subTitle2 = BLANK_STRING
        var subTitle3 = BLANK_STRING
        var subTitle4 = BLANK_STRING
        var subTitle5 = BLANK_STRING
        var isFormGenerated: Boolean = false
        surveyList.forEach { survey ->
            isFormGenerated = survey.isFormGenerated
            val selectedValue = getSelectedValue(survey.optionItems)
            when (survey.tagId) {
                SurveyCardTag.SURVEY_TAG_DATE.tag -> subTitle1 = selectedValue
                SurveyCardTag.SURVEY_TAG_AMOUNT.tag -> subTitle2 = selectedValue

                SurveyCardTag.SURVEY_TAG_NATURE.tag -> subTitle3 = selectedValue
                SurveyCardTag.SURVEY_TAG_MODE.tag -> subTitle4 = selectedValue
                SurveyCardTag.SURVEY_TAG_NO_OF_DIDI.tag -> subTitle5 = selectedValue
            }
        }
        val referenceId = surveyList.firstOrNull()?.referenceId ?: ""

        return SurveyUIModel(
            referenceId = referenceId,
            subTittle1 = subTitle1,
            subTittle2 = subTitle2,
            subTittle3 = subTitle3,
            subTittle4 = subTitle4,
            subTittle5 = subTitle5,
            isFormGenerated = isFormGenerated
        )
    }

    private fun getSelectedValue(optionItems: List<OptionsUiModel>): String {
        if (optionItems.isEmpty()) return BLANK_STRING

        val selectedValues = optionItems.filter { it.isSelected == true }
            .mapNotNull { item ->
                when {
                    item.optionType == QuestionType.DateType.name || item.optionType == QuestionType.InputNumber.name -> item.selectedValue
                    item.description != null -> item.description
                    else -> BLANK_STRING
                }
            }

        return if (selectedValues.isEmpty()) BLANK_STRING else selectedValues.joinToString(",")
    }

    fun deleteSurveyAnswer(referenceId: String, onDeleteSuccess: (deleteCount: Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val deleteCount = saveSurveyAnswerUseCase.deleteSurveyAnswer(
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId,
                referenceId = referenceId,
            )
            if (deleteCount > 0) {
                surveyAnswerEventWriterUseCase.deleteDisbursementOrReceiptOfFundEvent(

                    surveyID = surveyId,
                    sectionId = sectionId,
                    surveyName = BLANK_STRING,
                    grantId = grantConfigUi.value.grantId,
                    grantType = grantConfigUi.value.grantType,
                    referenceId = referenceId,
                    taskId = taskEntity?.taskId ?: DEFAULT_ID,
                    uriList = emptyList(),
                    taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    subjectType = subjectType

                )
                onDeleteSuccess(deleteCount)
            }
            formUseCase.deleteFormE(taskId = taskId, referenceId = referenceId)
            getTaskUseCase.updateTaskStatus(
                taskEntity?.taskId ?: taskId,
                SurveyStatusEnum.INPROGRESS.name
            )
            taskEntity = getTaskUseCase.getTask(taskId)
            matStatusEventWriterUseCase.updateTaskStatus(
                taskEntity!!,
                "CSG",
                subjectType = subjectType
            )
        }
    }

    fun setGrantComponentDTO() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            grantConfigUi.value = grantConfigUseCase.getGrantComponentDTO(
                surveyId = surveyId,
                activityConfigId = activityConfigId
            )

        }

    }

    fun saveButtonClicked() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            taskStatusUseCase.markTaskCompleted(
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                taskId = taskEntity?.taskId ?: DEFAULT_ID
            )
            taskEntity = getTaskUseCase.getTask(taskId)

            taskEntity?.let {
                matStatusEventWriterUseCase.updateTaskStatus(
                    taskEntity = it,
                    surveyName = "",
                    subjectType = subjectType
                )
            }
        }

    }

    private fun checkButtonValidation() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            isButtonEnable.value = isDoneButtonEnable()
            isAddDisbursementButtonEnable.value =
                (sanctionedAmount == 0 && !isActivityCompleted.value) || (sanctionedAmount != getTotalSubmittedAmount() && !isActivityCompleted.value)
        }
    }

    private fun isDoneButtonEnable(): Boolean {
        if (sanctionedAmount == 0) {
            return !isActivityCompleted.value && taskList.value.isNotEmpty()
        } else {
            return sanctionedAmount == getTotalSubmittedAmount() && !isActivityCompleted.value && isAllFormGeneratedDisburement()
        }
    }

    private fun isActivityCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isActivityCompleted.value = getActivityUseCase.isAllActivityCompleted(
                missionId = taskEntity?.missionId ?: 0,
                activityId = taskEntity?.activityId ?: 0
            )
            checkButtonValidation()
        }


    }

    fun getTotalSubmittedAmount(): Int {
        if (sanctionedAmount == 0) {
            return 0

        }
        totalSubmittedAmount = 0
        taskList.value.entries.forEach {
            totalSubmittedAmount +=
                it.value.filter { it.tagId == SurveyCardTag.SURVEY_TAG_AMOUNT.tag }
                    .sumOf { getSelectedValue(it.optionItems).toInt() } ?: 0

        }
        return totalSubmittedAmount
    }

    fun isAllFormGeneratedDisburement(): Boolean {
        taskList.value.entries.forEach {
            it.value.forEach {
                if (!it.isFormGenerated) {
                    return false
                }
            }


        }
        return true

    }


}