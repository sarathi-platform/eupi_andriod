package com.nrlm.baselinesurvey.ui.question_screen.presentation

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState

data class QuestionEntityState(
    val questionId: Int? = -1,
    val questionEntity: QuestionEntity?,
    val optionItemEntityState: List<OptionItemEntityState>,
    val answerdOptionList: List<OptionItemEntity>,
    val showQuestion: Boolean
) {
    companion object {
        fun getEmptyStateObject(): QuestionEntityState {
            return QuestionEntityState(
                questionEntity = null,
                optionItemEntityState = emptyList(),
                answerdOptionList = emptyList(),
                showQuestion = false
            )
        }
    }
}
