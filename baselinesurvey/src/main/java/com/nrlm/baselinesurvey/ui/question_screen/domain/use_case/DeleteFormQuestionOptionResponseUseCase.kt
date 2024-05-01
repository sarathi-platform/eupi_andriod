package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository

class DeleteFormQuestionOptionResponseUseCase(private val repository: FormQuestionResponseRepository) {

    suspend operator fun invoke(
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        surveyeeId: Int,
        referenceId: String
    ) {
        repository.deleteFormQuestionResponseForOption(
            optionId = optionId,
            questionId = questionId,
            sectionId = sectionId,
            surveyId = surveyId,
            surveyeeId = surveyeeId,
            referenceId = referenceId
        )
    }

}