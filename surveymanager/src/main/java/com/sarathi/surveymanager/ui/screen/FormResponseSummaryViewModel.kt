package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toSafeInt
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.usecase.caste.FetchCasteConfigNetworkUseCase
import com.nudge.core.value
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchInfoUiModelUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusEventWriterUserCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusUpdateUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots.Companion.CASTE_ID
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.MissionFilterUtils
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FormResponseSummaryViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val getSectionListUseCase: GetSectionListUseCase,
    private val sectionStatusUpdateUseCase: SectionStatusUpdateUseCase,
    private val sectionStatusEventWriterUserCase: SectionStatusEventWriterUserCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase,
    private val fetchCasteConfigNetworkUseCase: FetchCasteConfigNetworkUseCase,
    private val fetchInfoUiModelUseCase: FetchInfoUiModelUseCase,
    private val missionFilterUtils: MissionFilterUtils
) : BaseViewModel() {

    var surveyId: Int = 0
    var sectionId: Int = 0
    var taskId: Int = 0
    var formId: Int = 0
    var activityConfigId: Int = 0
    var taskEntity: ActivityTaskEntity? = null
    var activityConfig: ActivityConfigEntity? = null
    var isActivityCompleted = false

    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    private val _formQuestionResponseMap =
        mutableStateMapOf<Pair<String, Int>, List<SurveyAnswerFormSummaryUiModel>>()
    val formQuestionResponseMap: SnapshotStateMap<Pair<String, Int>, List<SurveyAnswerFormSummaryUiModel>> get() = _formQuestionResponseMap

    val referenceIdsList = mutableStateListOf<Pair<String, Int>>()

    var surveyConfig =
        mutableMapOf<String, List<SurveyCardModel>>()

    var activityInfoUIModel = ActivityInfoUIModel.getDefaultValue()

    fun init(
        taskId: Int,
        surveyId: Int,
        sectionId: Int,
        formId: Int,
        activityConfigId: Int
    ) {
        this.taskId = taskId
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.formId = formId
        this.activityConfigId = activityConfigId
    }

    override fun <T> onEvent(event: T) {
        when (event) {

            is InitDataEvent.InitDataState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                intiQuestions()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(isLoaderVisible = event.showLoader)
            }
        }
    }

    private fun intiQuestions() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskEntity = getTaskUseCase.getTask(taskId)
            if (_questionUiModel.value.isEmpty()) {
                _questionUiModel.value = fetchDataUseCase.invokeFormQuestions(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    subjectId = taskEntity?.subjectId.value(DEFAULT_ID),
                    activityConfigId = activityConfigId,
                    referenceId = BLANK_STRING,
                    grantId = NUMBER_ZERO,
                    formId = formId,
                    missionId = taskEntity?.missionId.value(DEFAULT_ID),
                    activityId = taskEntity?.activityId.value(DEFAULT_ID)
                )
            }

            initSurveyConfigAndGetSavedResponses()

            activityInfoUIModel = fetchInfoUiModelUseCase.fetchActivityInfo(
                activityConfig?.missionId.value(),
                activityConfig?.activityId.value()
            )

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun initSurveyConfigAndGetSavedResponses() {
        taskEntity?.let {
            activityConfig =
                getActivityUiConfigUseCase.getActivityConfig(it.activityId, it.missionId)

            val formQuestionIdList = fetchDataUseCase.invoke(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                activityConfigId = activityConfigId,
                referenceId = BLANK_STRING,
                grantId = NUMBER_ZERO,
                missionId = taskEntity?.missionId.value(DEFAULT_ID),
                activityId = taskEntity?.activityId.value(DEFAULT_ID)
            ).filter { it.formId != NUMBER_ZERO }.map { it.questionId }

            val savedAnswers = saveSurveyAnswerUseCase.getAllSaveAnswer(
                activityConfigId = activityConfigId,
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = it.taskId,
                grantId = NUMBER_ZERO
            )

            _formQuestionResponseMap.clear()
            _formQuestionResponseMap.putAll(
                savedAnswers
                    .filter { savedAnswer ->
                        savedAnswer.referenceId != BLANK_STRING && formQuestionIdList.contains(
                            savedAnswer.questionId
                        ) && savedAnswer.optionItems.isNotEmpty()
                    }
                    .groupBy { savedAnswer -> Pair(savedAnswer.referenceId, savedAnswer.formId) }
                    .filter { it.key.second == formId }
            )


            referenceIdsList.clear()
            referenceIdsList.addAll(
                formQuestionResponseMap.entries
                    .map { mapEntry -> // map formQuestionResponseMap Entries to a pair with map key as first and created data from map value.first()
                        Pair(
                            mapEntry.key,
                            mapEntry.value.firstOrNull()?.createdDate.value(Long.MAX_VALUE)
                        )
                    }
                    .sortedBy { pair: Pair<Pair<String, Int>, Long> -> pair.second } // sort the map of Pair on created date
                    .map { it.first } // convert the sorted list back to the list of formQuestionResponseMap keys.
            )

            getSurveyConfigFromDbUseCase.invoke(it.missionId, it.activityId, surveyId, formId)
                .also { surveyConfigEntityList ->
                    val taskAttributes = getTaskUseCase.getSubjectAttributes(it.taskId)
                    getSurveyConfig(surveyConfigEntityList, taskAttributes)
                }

            isActivityCompleted = getActivityUseCase.isActivityCompleted(
                missionId = it.missionId.value(NUMBER_ZERO),
                activityId = it.activityId.value(NUMBER_ZERO)
            )
        }
    }

    private suspend fun getSurveyConfig(
        surveyConfigEntityList: List<SurveyConfigEntity>,
        taskAttributes: List<SubjectAttributes>
    ) {
        val mSurveyConfig = mutableMapOf<String, List<SurveyCardModel>>()
        /*surveyConfigEntityList.forEach { surveyConfigEntity ->
            mSurveyConfig.put(
                surveyConfigEntity.key,
                SurveyCardModel.getSurveyCarModel(surveyConfigEntity)
            )
        }*/
        surveyConfigEntityList
            .groupBy { it.key }
            .mapValues { (key, entities) ->
                mSurveyConfig.put(key, entities.map { entity ->
                    val model = if (entity.type.equals(UiConfigAttributeType.DYNAMIC.name, true)) {
                        if (entity.value.equals(CASTE_ID, true)) {
                            val casteId =
                                taskAttributes.find { it.key == entity.value }?.value.value()
                                    .toSafeInt()
                            entity.copy(
                                value = fetchCasteConfigNetworkUseCase.getCasteIdValue(casteId = casteId)
                                    ?: BLANK_STRING
                            )
                        } else {
                            entity.copy(value = taskAttributes.find { it.key == entity.value }?.value.value())
                        }
                    } else {
                        entity
                    }
                    SurveyCardModel.getSurveyCarModel(model)
                })

            }
        surveyConfig = mSurveyConfig
    }

    fun deleteAnswer(referenceId: Pair<String?, Int>?) {
        ioViewModelScope {
            referenceIdsList.remove(referenceId)

            val deleteCount = saveSurveyAnswerUseCase.deleteSurveyAnswer(
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId,
                referenceId = referenceId?.first.value(),
            )

            if (deleteCount > 0) {
                surveyAnswerEventWriterUseCase.deleteSavedAnswerEvent(
                    surveyID = surveyId,
                    sectionId = sectionId,
                    surveyName = BLANK_STRING,
                    grantId = NUMBER_ZERO,
                    grantType = BLANK_STRING,
                    referenceId = referenceId?.first.value(),
                    taskId = taskEntity?.taskId ?: DEFAULT_ID,
                    uriList = emptyList(),
                    taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    subjectType = activityConfig?.subject.value(),
                    isFromRegenerate = false
                )
            }
            updateMissionFilter()
            updateTaskStatus(taskId = taskId)
            checkAndUpdateSectionStatus(
                missionId = activityConfig?.missionId.value(),
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId,
                status = SurveyStatusEnum.INPROGRESS.name
            )
        }
    }

    suspend fun updateTaskStatus(taskId: Int) {
        taskEntity?.let { task ->
            if (task.status != SurveyStatusEnum.INPROGRESS.name) {
                val surveyEntity = getSectionListUseCase.getSurveyEntity(surveyId)
                surveyEntity?.let { survey ->
                    taskStatusUseCase.markTaskInProgress(taskId = taskId)
                    matStatusEventWriterUseCase.updateTaskStatus(
                        taskEntity = task,
                        surveyName = survey.surveyName,
                        subjectType = activityConfig?.subject.value()
                    )
                }
            }
        }
    }

    suspend fun checkAndUpdateSectionStatus(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        val existingSectionStatus =
            getSectionListUseCase.getSectionStatusMap(missionId, surveyId, taskId)[sectionId]
        existingSectionStatus?.let { sectionStatus ->
            if (sectionStatus != SurveyStatusEnum.INPROGRESS.name) {
                updateSectionStatus(missionId, surveyId, sectionId, taskId, status)
            }
        } ?: suspend {
            updateSectionStatus(missionId, surveyId, sectionId, taskId, status)
        }


    }

    suspend fun updateSectionStatus(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        sectionStatusUpdateUseCase.invoke(
            missionId = missionId,
            surveyId = surveyId,
            sectionId = sectionId,
            taskId = taskId,
            status = status
        )
        sectionStatusEventWriterUserCase.invoke(
            surveyId = surveyId,
            sectionId = sectionId,
            taskId = taskId,
            status = status, isFromRegenerate = false
        )
    }

    fun getAESSecretKey(): String {
        return fetchAppConfigFromCacheOrDbUsecase.getAESSecretKey()
    }

    fun updateMissionFilter() {
        missionFilterUtils.updateMissionFilterOnUserAction(activityInfoUIModel)
    }

}