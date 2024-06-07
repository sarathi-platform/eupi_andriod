package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

interface ISurveyAnswerEventRepository {
    suspend fun writeSaveAnswerEvent(
        questionUiModel: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String
    ): SaveAnswerEventDto

}