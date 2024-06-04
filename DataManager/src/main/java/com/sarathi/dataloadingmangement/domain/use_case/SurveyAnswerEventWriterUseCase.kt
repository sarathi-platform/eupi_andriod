package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.ISurveyAnswerEventRepository

class SurveyAnswerEventWriterUseCase(
    private val repository: ISurveyAnswerEventRepository
) {
    suspend operator fun invoke(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: Int,
        taskLocalId: String
    ) {

        val saveAnswerEventDto = repository.writeSaveAnswerEvent(
            questionUiModel,
            subjectId,
            subjectType,
            refrenceId,
            taskLocalId
        )

    }

}