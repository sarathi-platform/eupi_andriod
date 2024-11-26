package com.sarathi.dataloadingmangement.domain.use_case

import android.util.Log
import com.nudge.core.DEFAULT_ID
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.getFileNameFromURL
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.RegenerateLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.AttendanceEventWriterUseCase
import com.sarathi.dataloadingmangement.model.events.BaseSaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.RegenerateGrantEventRepositoryImpl
import javax.inject.Inject

class RegenerateGrantEventUsecase @Inject constructor(
    private val regenerateGrantEventRepositoryImpl: RegenerateGrantEventRepositoryImpl,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val documentEventWriterUseCase: DocumentEventWriterUseCase,
    private val attendanceEventWriterUseCase: AttendanceEventWriterUseCase,
    private val regenerateLivelihoodEventUseCase: RegenerateLivelihoodEventUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
) {


    suspend fun invoke() {

        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                "regenerate_" + coreSharedPrefs.getMobileNo(),
                coreSharedPrefs.getUserType()
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                "regenerate_" + coreSharedPrefs.getMobileNo(),
                coreSharedPrefs.getUserType()
            )
        )
        writeMissionStatusEvent()
        writeActivityStatusEvent()
        writeTaskStatusEvent()
        writeIncomeExpenseEvent()
        writeSurveyAnswerEvents()
        writeFormUpdateEvent()
        writeDocumentUploadEvent()
        writeAttendanceEvent()
    }

    private suspend fun writeAttendanceEvent() {
        attendanceEventWriterUseCase.invoke(isFromRegenerate = true)
    }

    private suspend fun writeMissionStatusEvent() {

        regenerateGrantEventRepositoryImpl.getAllMissionsForUser().forEach {
            matStatusEventWriterUseCase.updateMissionStatus(
                missionEntity = it,
                "",
                isFromRegenerate = true
            )
        }
    }

    private suspend fun writeActivityStatusEvent() {
        regenerateGrantEventRepositoryImpl.getAllActivityForUser().forEach {

            matStatusEventWriterUseCase.updateActivityStatus(
                activityEntity = it,
                surveyName = "",
                isFromRegenerate = true
            )
        }

    }

    private suspend fun writeTaskStatusEvent() {
        regenerateGrantEventRepositoryImpl.getAllTaskForUser().forEach {
            val subjectType = regenerateGrantEventRepositoryImpl.getSubjectTypeForActivity(
                activityId = it.activityId,
                missionId = it.missionId
            )
            matStatusEventWriterUseCase.updateTaskStatus(
                taskEntity = it,
                subjectType = subjectType,
                surveyName = "",
                isFromRegenerate = true
            )

        }
    }

    private suspend fun writeSurveyAnswerEvents() {
        try {
            val surveyAnswers = regenerateGrantEventRepositoryImpl.getAllSurveyAnswerForUSer()
                .distinctBy { it.referenceId }
            surveyAnswers.forEach { surveyAnswer ->
                val taskEntity =
                    regenerateGrantEventRepositoryImpl.getTaskEntity(surveyAnswer.taskId)
                val (questionUiModel, subjectType, activityConfig) = findQuestionUiListAndActivityConfig(
                    surveyAnswer,
                    taskEntity
                )
                surveyAnswerEventWriterUseCase.invoke(
                    questionUiModels = questionUiModel,
                    taskId = surveyAnswer.taskId,
                    subjectId = surveyAnswer.subjectId,
                    referenceId = surveyAnswer.referenceId,
                    grantId = surveyAnswer.grantId,
                    grantType = surveyAnswer.grantType,
                    taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                    subjectType = subjectType,
                    isFromRegenerate = true,
                    activityId = activityConfig?.activityId.value(),
                    activityReferenceId = activityConfig?.referenceId,
                    activityReferenceType = activityConfig?.referenceType
                )

            }
        } catch (exception: Exception) {
            Log.e("Regenerate", exception.localizedMessage)
        }

    }

    private suspend fun findQuestionUiListAndActivityConfig(
        surveyAnswer: SurveyAnswerEntity,
        taskEntity: ActivityTaskEntity?
    ): Triple<List<QuestionUiModel>, String, ActivityConfigEntity?> {
        val questionUiModel = fetchDataUseCase.invoke(
            surveyId = surveyAnswer.surveyId,
            sectionId = surveyAnswer.sectionId,
            subjectId = surveyAnswer.subjectId,
            referenceId = surveyAnswer.referenceId,
            activityConfigId = taskEntity?.activityId ?: -1,
            grantId = surveyAnswer.grantId,
            activityId = taskEntity?.activityId.value(DEFAULT_ID),
            missionId = taskEntity?.missionId.value(DEFAULT_ID),
            isFromRegenerate = true
        )
        val subjectType = regenerateGrantEventRepositoryImpl.getSubjectTypeForActivity(
            activityId = taskEntity?.activityId ?: -1,
            missionId = taskEntity?.missionId ?: -1
        )
        val activityConfig = getActivityUiConfigUseCase.getActivityConfig(
            taskEntity!!.activityId,
            taskEntity.missionId
        )
        return Triple(questionUiModel, subjectType, activityConfig)
    }

    /**
     * Fetch all survey answers for Survey Type Activity
     * Create QuestionUiModel using Activity and Question details
     */
    suspend fun fetchSurveyAnswerEvents(): ArrayList<BaseSaveAnswerEventDto>? {
        try {
            val surveyList = arrayListOf<BaseSaveAnswerEventDto>()
            val surveyAnswers = regenerateGrantEventRepositoryImpl.getAllSurveyAnswerForUSer()
            surveyAnswers.forEach { surveyAnswer ->
                val taskEntity =
                    regenerateGrantEventRepositoryImpl.getTaskEntity(surveyAnswer.taskId)
                taskEntity?.let { task ->
                    var (questionUiModel, subjectType, activityConfig) = findQuestionUiListAndActivityConfig(
                        surveyAnswer,
                        taskEntity
                    )
                    questionUiModel =
                        questionUiModel.filter { it.options?.any { it.isSelected == true } == true }

                    val eventList = arrayListOf<BaseSaveAnswerEventDto>()
                    questionUiModel.find { it.questionId == surveyAnswer.questionId && it.sectionId == surveyAnswer.sectionId && it.surveyId == surveyAnswer.surveyId }
                        ?.let {
                            val event = surveyAnswerEventWriterUseCase.fetchQuestionAnswerEventList(
                                questionUiModel = it,
                                taskId = surveyAnswer.taskId,
                                subjectId = surveyAnswer.subjectId,
                                referenceId = surveyAnswer.referenceId,
                                grantId = surveyAnswer.grantId,
                                grantType = surveyAnswer.grantType,
                                taskLocalId = task.localTaskId ?: BLANK_STRING,
                                subjectType = subjectType,
                                activityId = activityConfig?.activityId.value(),
                                activityReferenceId = activityConfig?.referenceId,
                                activityReferenceType = activityConfig?.referenceType
                            )
                            eventList.add(event)
                        }

                    if (eventList.isNotEmpty()) {
                        surveyList.addAll(eventList)
                    }

                }
            }
            return surveyList
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return null
    }

    private suspend fun writeFormUpdateEvent() {
        regenerateGrantEventRepositoryImpl.getAllFormData().forEach {
            formEventWriterUseCase.writeFormEvent("", it, isFromRegenerate = true)

        }
    }

    private suspend fun writeDocumentUploadEvent() {
        regenerateGrantEventRepositoryImpl.getDocumentData().forEach { document ->
            document.documentValue.split(DELEGATE_COMM).forEach {


                documentEventWriterUseCase.writeSaveDocumentEvent(
                    generatedDate = document.generateDate.toString(),
                    documentType = document.documentType,
                    documentName = getFileNameFromURL(it),
                    activityId = document.activityId,
                    isFromRegenerate = true
                )
            }
        }
    }

    private suspend fun writeIncomeExpenseEvent() {
        regenerateLivelihoodEventUseCase.invoke()
    }

}