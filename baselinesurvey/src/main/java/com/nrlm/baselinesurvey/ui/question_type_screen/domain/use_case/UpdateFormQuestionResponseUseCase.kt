package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository

class UpdateFormQuestionResponseUseCase(private val repository: FormQuestionResponseRepository) {

    suspend operator fun invoke(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String,
        referenceId: String,
        didiId: Int,
        selectedValueId: List<Int>
    ) {
        return repository.updateOptionItemValue(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            optionId = optionId,
            selectedValue = selectedValue,
            referenceId = referenceId,
            didiId = didiId,
            selectedValueIds = selectedValueId
        )
    }

}