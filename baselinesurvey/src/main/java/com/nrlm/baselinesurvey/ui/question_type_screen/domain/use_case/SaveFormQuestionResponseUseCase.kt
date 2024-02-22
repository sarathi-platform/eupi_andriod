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

    suspend fun saveFormsListIntoDB(
        formQuestionResponseEntity: List<FormQuestionResponseEntity>
    ) {
        repository.saveFormsIntoDB(
            formQuestionResponseEntity
        )
    }

    suspend fun updateFromListItemIntoDb(
        formQuestionResponseEntity: FormQuestionResponseEntity
    ) {
        repository.updateFromListItemIntoDb(formQuestionResponseEntity)
    }

    suspend fun getOptionItem(formQuestionResponseEntity: FormQuestionResponseEntity): Int {
        return repository.getOptionItem(formQuestionResponseEntity)
    }

}