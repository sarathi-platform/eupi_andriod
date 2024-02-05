package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

interface QuestionTypeRepository {
    suspend fun getQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntity>

    suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String
    )
}