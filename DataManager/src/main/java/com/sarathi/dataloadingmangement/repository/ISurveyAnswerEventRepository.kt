package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
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
        taskId: Int
    ): SaveAnswerMoneyJorunalEventDto

    suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int
    ): SaveAnswerEventDto


}