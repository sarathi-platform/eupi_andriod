package com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

data class FormTypeOption(
    var surveyId: Int,
    var didiId: Int,
    var sectionId: Int,
    val questionId: Int,
    var options: List<OptionItemEntity>
) {

    companion object {
        fun getOptionItem(
            surveyId: Int,
            didiId: Int,
            sectionId: Int,
            questionId: Int,
            optionItems: List<OptionItemEntity>
        ): FormTypeOption {
            return FormTypeOption(surveyId, didiId, sectionId, questionId, optionItems)
        }
    }
}