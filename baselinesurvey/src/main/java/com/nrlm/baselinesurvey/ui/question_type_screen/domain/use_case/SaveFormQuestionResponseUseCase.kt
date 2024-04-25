package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository.FormQuestionResponseRepository
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState

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
        formQuestionResponseEntity.forEach { formQuestionResponseEntity ->
            formQuestionResponseEntity.userId = repository.getBaseLineUserId()
        }
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

    suspend fun deleteFormResponseForOption(didiId: Int, optionItem: OptionItemEntityState?) {

    }

}