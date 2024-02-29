package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository

class DeleteFormQuestionResponseUseCase (private val repository: FormQuestionResponseRepository) {

    suspend operator fun invoke(referenceId: String) {
        repository.deleteFormQuestionResponseForReferenceId(referenceId = referenceId)
    }

}