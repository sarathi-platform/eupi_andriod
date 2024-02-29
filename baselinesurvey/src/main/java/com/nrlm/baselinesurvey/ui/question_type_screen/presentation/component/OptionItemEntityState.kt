package com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

data class OptionItemEntityState(
    val optionId: Int? = -1,
    val optionItemEntity: OptionItemEntity?,
    val showQuestion: Boolean
) {
    companion object {
        fun getEmptyStateObject(): OptionItemEntityState {
            return OptionItemEntityState(
                optionItemEntity = null,
                showQuestion = false
            )
        }
    }
}
