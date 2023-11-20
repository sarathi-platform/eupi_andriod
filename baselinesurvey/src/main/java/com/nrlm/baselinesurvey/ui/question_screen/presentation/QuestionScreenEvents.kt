package com.nrlm.baselinesurvey.ui.question_screen.presentation

import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.utils.SectionStatus

sealed class QuestionScreenEvents {
    data class RatioTypeQuestionAnswered(val surveyId: Int, val sectionId: Int, val didiId: Int, val questionId: Int, val optionsItem: OptionsItem, val questionEntity: QuestionEntity) : QuestionScreenEvents()
    data class ListTypeQuestionAnswered(val surveyId: Int, val sectionId: Int, val didiId: Int, val questionId: Int, val optionsItem: OptionsItem, val questionEntity: QuestionEntity) : QuestionScreenEvents()
    data class GridTypeQuestionAnswered(val surveyId: Int, val sectionId: Int, val didiId: Int, val questionId: Int, val optionsItems: List<OptionsItem>, val questionEntity: QuestionEntity) : QuestionScreenEvents()
    data class SectionProgressUpdated(val surveyId: Int, val sectionId: Int, val didiId: Int, val sectionStatus: SectionStatus): QuestionScreenEvents()
    data class SendAnswersToServer(val surveyId: Int, val sectionId: Int, val didiId: Int) : QuestionScreenEvents()
}
