package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository

class SaveFormQuestionResponseUseCase(private val repository: FormQuestionResponseRepository) {

    suspend operator fun invoke(
        formQuestionResponseEntity: FormQuestionResponseEntity
    ) {
        repository.addFormResponseForQuestion(
            formQuestionResponseEntity
        )
    }
}