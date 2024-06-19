package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.model.events.SaveFormAnswerEventDto
import javax.inject.Inject

class FormEventRepositoryImpl @Inject constructor() : IFormEventRepository {
    override fun getSaveFormAnswerEventDto(
        formEntity: FormEntity
    ): SaveFormAnswerEventDto {
        return SaveFormAnswerEventDto(
            subjectId = formEntity.subjectid,
            surveyId = formEntity.surveyId,
            formType = formEntity.formType,
            activityId = formEntity.activityId,
            localReferenceId = formEntity.localReferenceId,
            taskId = formEntity.taskid,
            formGenerated = formEntity.isFormGenerated,
            generatedDate = formEntity.formGenerateDate
        )
    }
}