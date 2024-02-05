package com.nrlm.baselinesurvey.ui.question_type_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import javax.inject.Inject

class QuestionTypeRepositoryImpl @Inject constructor(
    private val optionItemDao: OptionItemDao
) : QuestionTypeRepository {
    override suspend fun getQuestionOption(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
    ): List<OptionItemEntity> {
        return optionItemDao.getSurveySectionQuestionOptions(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId
        )
    }

    override suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String
    ) {
        return optionItemDao.updateOptionItemValue(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            optionId = optionId,
            selectValue = selectedValue
        )
    }

}

