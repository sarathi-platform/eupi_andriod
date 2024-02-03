package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.QuestionTypeRepository

class GetQuestionTypeFormOptionUseCase(private val repository: QuestionTypeRepository) {
    suspend operator fun invoke(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntity> {
        return repository.getQuestionOption(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId
        )
    }

}