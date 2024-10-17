package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.events.BaseSaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.DeleteAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveyAnswerEventRepository {
    suspend fun writeMoneyJournalSaveAnswerEvent(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        sectionTagId: List<Int>
    ): SaveAnswerMoneyJorunalEventDto

    suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        activityId: Int,
        activityReferenceId: Int?,
        activityReferenceType: String?
    ): BaseSaveAnswerEventDto

    suspend fun writeDeleteSaveAnswerEvent(
        surveyID: Int,
        sectionId: Int,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int
    ): DeleteAnswerEventDto

    suspend fun getTagIdForSection(sectionId: Int): List<Int>
}