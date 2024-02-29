package com.nrlm.baselinesurvey.ui.question_screen.presentation

import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.utils.states.SectionStatus

sealed class QuestionScreenEvents {
    data class RatioTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val optionItemId: Int,
        val questionEntity: QuestionEntity,
        val optionItemEntity: OptionItemEntity
    ) : QuestionScreenEvents()

    data class ListTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val optionItemId: Int,
        val questionEntity: QuestionEntity,
        val optionItemEntity: OptionItemEntity
    ) : QuestionScreenEvents()

    data class GridTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val questionEntity: QuestionEntity,
        val optionItemList: List<OptionItemEntity>
    ) : QuestionScreenEvents()

    data class SectionProgressUpdated(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val sectionStatus: SectionStatus
    ) : QuestionScreenEvents()

    data class FormTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val optionItemId: Int,
        val selectedValue: String,
    ) : QuestionScreenEvents()

    data class InputTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val optionItemId: Int,
        val inputValue: String,
    ) : QuestionScreenEvents()

    data class SendAnswersToServer(val surveyId: Int, val sectionId: Int, val didiId: Int) :
        QuestionScreenEvents()

    data class SaveMiscTypeQuestionAnswers(
        val surveyeeId: Int,
        val questionEntityState: QuestionEntityState,
        val optionItemEntity: OptionItemEntity,
        val selectedValue: String
    ) : QuestionScreenEvents()

    data class UpdateQuestionAnswerMappingForUi(
        val question: QuestionEntityState,
        val mOptionItem: List<OptionItemEntity>
    ) : QuestionScreenEvents()

    data class UpdateInputTypeQuestionAnswerEntityForUi(val inputTypeQuestionAnswerEntity: InputTypeQuestionAnswerEntity)

    data class UpdateAnsweredQuestionCount(
        val question: QuestionEntityState,
        val isAllMultipleTypeQuestionUnanswered: Boolean = false
    )
}
