package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.getFileNameFromURL
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.AttendanceEventWriterUseCase
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
    private val coreSharedPrefs: CoreSharedPrefs
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
        writeSurveyAnswerEvents()
        writeFormUpdateEvent()
        writeDocumentUploadEvent()
        writeAttendanceEvent()
    }

    private suspend fun writeAttendanceEvent() {
        attendanceEventWriterUseCase.invoke()
    }

    private suspend fun writeMissionStatusEvent() {

        regenerateGrantEventRepositoryImpl.getAllMissionsForUser().forEach {
            matStatusEventWriterUseCase.updateMissionStatus(missionEntity = it, "")
        }
    }

    private suspend fun writeActivityStatusEvent() {
        regenerateGrantEventRepositoryImpl.getAllActivityForUser().forEach {

            matStatusEventWriterUseCase.updateActivityStatus(activityEntity = it, surveyName = "")
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
                surveyName = ""
            )

        }
    }

    private suspend fun writeSurveyAnswerEvents() {
        val surveyAnswers = regenerateGrantEventRepositoryImpl.getAllSurveyAnswerForUSer()
            .distinctBy { it.referenceId }
        surveyAnswers.forEach { surveyAnswer ->
            val taskEntity = regenerateGrantEventRepositoryImpl.getTaskEntity(surveyAnswer.taskId)
            val questionUiModel = fetchDataUseCase.invoke(
                surveyId = surveyAnswer.surveyId,
                sectionId = surveyAnswer.sectionId,
                subjectId = surveyAnswer.subjectId,
                referenceId = surveyAnswer.referenceId,
                activityConfigId = taskEntity.activityId,
                grantId = surveyAnswer.grantId
            )
            val subjectType = regenerateGrantEventRepositoryImpl.getSubjectTypeForActivity(
                activityId = taskEntity.activityId,
                missionId = taskEntity.missionId
            )
            surveyAnswerEventWriterUseCase.invoke(
                questionUiModels = questionUiModel,
                taskId = surveyAnswer.taskId,
                subjectId = surveyAnswer.subjectId,
                referenceId = surveyAnswer.referenceId,
                grantId = surveyAnswer.grantId,
                grantType = surveyAnswer.grantType,
                taskLocalId = taskEntity.localTaskId,
                subjectType = subjectType
            )

        }


    }

    private suspend fun writeFormUpdateEvent() {
        regenerateGrantEventRepositoryImpl.getAllFormData().forEach {
            formEventWriterUseCase.writeFormEvent("", it)

        }
    }

    private suspend fun writeDocumentUploadEvent() {
        regenerateGrantEventRepositoryImpl.getDocumentData().forEach { document ->
            document.documentValue.split(DELEGATE_COMM).forEach {


                documentEventWriterUseCase.writeSaveDocumentEvent(
                    generatedDate = document.generateDate.toString(),
                    documentType = document.documentType,
                    documentName = getFileNameFromURL(it),
                    activityId = document.activityId
                )
            }
        }
    }

}