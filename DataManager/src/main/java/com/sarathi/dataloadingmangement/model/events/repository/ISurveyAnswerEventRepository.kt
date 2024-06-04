package com.sarathi.dataloadingmangement.model.events.repository

import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveyAnswerEventRepository {
    suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: Int,
        taskLocalId: String
    ): SaveAnswerEventDto

}