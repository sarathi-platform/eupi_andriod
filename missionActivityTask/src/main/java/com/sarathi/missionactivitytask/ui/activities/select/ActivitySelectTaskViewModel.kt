package com.sarathi.missionactivitytask.ui.activities.select

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.DEFAULT_ID
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class ActivitySelectTaskViewModel @Inject constructor(
    private val getActivityTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val grantConfigUseCase: GrantConfigUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase
) : TaskScreenViewModel(
    getActivityTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase
) {

    var referenceId: String = BLANK_STRING
    var grantID: Int = 0
    var grantType: String = BLANK_STRING
    var taskUiList = mutableStateOf<List<TaskUiModel>>(emptyList())
    val questionList = arrayListOf<QuestionUiModel>()
    private val _questionUiModel = mutableStateOf<HashMap<Int, QuestionUiModel>>(hashMapOf())
    val questionUiModel: State<HashMap<Int, QuestionUiModel>> get() = _questionUiModel

    val expandedIds = mutableStateListOf<Int>()
    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitActivitySelectTaskScreenState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                initActivitySelectTaskScreen(event.missionId, event.activityId)
            }
        }
    }

    private fun initActivitySelectTaskScreen(missionId: Int, activityId: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            taskUiList.value =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            taskUiList.value.forEach { task ->
                val list = intiQuestions(
                    taskId = task.taskId,
                    surveyId = activityConfigUiModel?.surveyId ?: 0,
                    activityConfigId = activityConfigUiModel?.activityConfigId ?: 0,
                    sectionId = activityConfigUiModel?.sectionId ?: 0,
                    grantID = 0,
                    referenceId = BLANK_STRING
                ).firstOrNull()
                list?.let {
                    val jsonString = "{\n" +
                            "  \"display\": \"Have you collected the materials - sticks, rope, cement - for the shed\",\n" +
                            "  \"formId\": 0,\n" +
                            "  \"isMandatory\": true,\n" +
                            "  \"languageId\": \"en\",\n" +
                            "  \"options\": [\n" +
                            "    {\n" +
                            "      \"conditions\": [],\n" +
                            "      \"contentEntities\": [],\n" +
                            "      \"description\": \"Option 1\",\n" +
                            "      \"isSelected\": false,\n" +
                            "      \"optionId\": 11,\n" +
                            "      \"optionType\": \"\",\n" +
                            "      \"order\": 1,\n" +
                            "      \"paraphrase\": \"Option 1\",\n" +
                            "      \"questionId\": 56,\n" +
                            "      \"sectionId\": 12,\n" +
                            "      \"selectedValue\": \"\",\n" +
                            "      \"selectedValueId\": 0,\n" +
                            "      \"surveyId\": 6\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"conditions\": [],\n" +
                            "      \"contentEntities\": [],\n" +
                            "      \"description\": \"Option 2\",\n" +
                            "      \"isSelected\": false,\n" +
                            "      \"optionId\": 12,\n" +
                            "      \"optionType\": \"\",\n" +
                            "      \"order\": 2,\n" +
                            "      \"paraphrase\": \"Option 2\",\n" +
                            "      \"questionId\": 56,\n" +
                            "      \"sectionId\": 12,\n" +
                            "      \"selectedValue\": \"\",\n" +
                            "      \"selectedValueId\": 0,\n" +
                            "      \"surveyId\": 6\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"conditions\": [],\n" +
                            "      \"contentEntities\": [],\n" +
                            "      \"description\": \"Option 3\",\n" +
                            "      \"isSelected\": false,\n" +
                            "      \"optionId\": 13,\n" +
                            "      \"optionType\": \"\",\n" +
                            "      \"order\": 2,\n" +
                            "      \"paraphrase\": \"Option 3\",\n" +
                            "      \"questionId\": 56,\n" +
                            "      \"sectionId\": 12,\n" +
                            "      \"selectedValue\": \"\",\n" +
                            "      \"selectedValueId\": 0,\n" +
                            "      \"surveyId\": 6\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"conditions\": [],\n" +
                            "      \"contentEntities\": [],\n" +
                            "      \"description\": \"Option 4\",\n" +
                            "      \"isSelected\": false,\n" +
                            "      \"optionId\": 14,\n" +
                            "      \"optionType\": \"\",\n" +
                            "      \"order\": 2,\n" +
                            "      \"paraphrase\": \"Option 4\",\n" +
                            "      \"questionId\": 56,\n" +
                            "      \"sectionId\": 12,\n" +
                            "      \"selectedValue\": \"\",\n" +
                            "      \"selectedValueId\": 0,\n" +
                            "      \"surveyId\": 6\n" +
                            "    }\n" +
                            "    \n" +
                            "  ],\n" +
                            "  \"questionDisplay\": \"Have you collected the materials - sticks, rope, cement - for the shed\",\n" +
                            "  \"questionId\": 56,\n" +
                            "  \"questionSummary\": \"Have you collected the materials - sticks, rope, cement - for the shed\",\n" +
                            "  \"sectionId\": 12,\n" +
                            "  \"subjectId\": 7572,\n" +
                            "  \"subjectType\": \"\",\n" +
                            "  \"summary\": \"\",\n" +
                            "  \"surveyId\": 6,\n" +
                            "  \"surveyName\": \"Organise Shed Materials\",\n" +
                            "  \"tagId\": [\n" +
                            "    104\n" +
                            "  ],\n" +
                            "  \"type\": \"RadioButton\"\n" +
                            "}"
                    it.subjectId = task.subjectId
                    val ls: ArrayList<OptionsUiModel> = arrayListOf()
                    it.options?.let { it1 -> ls.addAll(it1) }
                    ls.add(
                        OptionsUiModel(
                            isSelected = false,
                            selectedValue = BLANK_STRING,
                            selectedValueId = -1,
                            optionId = 12,
                            sectionId = 12,
                            surveyId = 6,
                            originalValue = BLANK_STRING,
                            questionId = 56,
                            optionType = BLANK_STRING,
                            conditions = emptyList(),
                            order = 3,
                            contentEntities = emptyList(),
                            paraphrase = "Option 1",
                            description = "Option 1"
                        )
                    )

                    ls.add(
                        OptionsUiModel(
                            isSelected = false,
                            selectedValue = BLANK_STRING,
                            selectedValueId = -1,
                            optionId = 13,
                            sectionId = 12,
                            surveyId = 6,
                            originalValue = BLANK_STRING,
                            questionId = 56,
                            optionType = BLANK_STRING,
                            conditions = emptyList(),
                            order = 4,
                            contentEntities = emptyList(),
                            paraphrase = "Option 2",
                            description = "Option 2"
                        )
                    )

                    ls.add(
                        OptionsUiModel(
                            isSelected = false,
                            selectedValue = BLANK_STRING,
                            selectedValueId = -1,
                            optionId = 14,
                            sectionId = 12,
                            surveyId = 6,
                            originalValue = BLANK_STRING,
                            questionId = 56,
                            optionType = BLANK_STRING,
                            conditions = emptyList(),
                            order = 5,
                            contentEntities = emptyList(),
                            paraphrase = "Option 3",
                            description = "Option 3"
                        )
                    )

                    ls.add(
                        OptionsUiModel(
                            isSelected = false,
                            selectedValue = BLANK_STRING,
                            selectedValueId = -1,
                            optionId = 18,
                            sectionId = 12,
                            surveyId = 6,
                            originalValue = BLANK_STRING,
                            questionId = 56,
                            optionType = BLANK_STRING,
                            conditions = emptyList(),
                            order = 6,
                            contentEntities = emptyList(),
                            paraphrase = "Option 4",
                            description = "Option 4"
                        )


                    )
                    it.options = ls
                    _questionUiModel.value[task.taskId ?: -1] = it
                }
            }
            withContext(CoreDispatchers.mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun intiQuestions(
        taskId: Int,
        surveyId: Int,
        sectionId: Int,
        activityConfigId: Int,
        referenceId: String,
        grantID: Int
    ): List<QuestionUiModel> {
        val taskEntity = getTaskUseCase.getTask(taskId)
        val questions = fetchDataUseCase.invoke(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
            activityConfigId = activityConfigId,
            referenceId = referenceId,
            grantId = grantID
        )
        return questions
    }

    fun saveSingleAnswerIntoDb(
        currentQuestionUiModel: QuestionUiModel,
        subjectType: String,
        taskId: Int
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val taskEntity = getTaskUseCase.getTask(taskId)
            saveQuestionAnswerIntoDb(currentQuestionUiModel, taskEntity)
            surveyAnswerEventWriterUseCase.saveSurveyAnswerEvent(
                questionUiModel = currentQuestionUiModel,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = grantType,
                taskId = taskId,
                uriList = ArrayList()
            )
        }
    }

    suspend fun saveQuestionAnswerIntoDb(
        question: QuestionUiModel,
        taskEntity: ActivityTaskEntity
    ) {
        saveSurveyAnswerUseCase.saveSurveyAnswer(
            questionUiModel = question,
            subjectId = taskEntity.subjectId ?: DEFAULT_ID,
            taskId = taskEntity.taskId,
            referenceId = referenceId,
            grantId = grantID,
            grantType = grantType
        )
    }


}