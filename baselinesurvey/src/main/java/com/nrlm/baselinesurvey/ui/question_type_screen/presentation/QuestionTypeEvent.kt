package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState

sealed class QuestionTypeEvent {
    data class SaveFormQuestionResponseEvent(
        val formQuestionResponseEntity: FormQuestionResponseEntity
    ) : QuestionTypeEvent()

    data class DeleteFormQuestionResponseEvent(
        val surveyId: Int,
        val sectionId: Int,
        val questionId: Int,
        val surveyeeId: Int,
        val referenceId: String,
        val questionDesc: String,
    ) : QuestionTypeEvent()

    data class SaveCacheFormQuestionResponseToDbEvent(
        val surveyId: Int,
        val sectionId: Int,
        val questionId: Int,
        val subjectId: Int,
        val formQuestionResponseList: List<FormQuestionResponseEntity>
    ) : QuestionTypeEvent()

    data class DeleteFormQuestionOptionResponseEvent(
        val optionId: Int?,
        val questionId: Int?,
        val sectionId: Int?,
        val surveyId: Int?,
        val surveyeeId: Int?,
        val referenceId: String
    ): QuestionTypeEvent()

    data class CacheFormQuestionResponseEvent(
        val formQuestionResponseEntity: FormQuestionResponseEntity
    ): QuestionTypeEvent()

    data class UpdateConditionalOptionState(val optionItemEntityState: OptionItemEntityState?, val userInputValue: String)

    data class UpdateConditionQuestionStateForUserInput(
        val questionEntityState: QuestionEntityState?,
        val userInputValue: String
    )

    data class UpdateConditionQuestionStateForSingleOption(
        val questionEntityState: QuestionEntityState?,
        val optionItemEntity: OptionItemEntity
    )

    data class UpdateConditionQuestionStateForMultipleOption(
        val questionEntityState: QuestionEntityState?,
        val optionItemEntityList: List<OptionItemEntity>
    )

    data class UpdateConditionQuestionStateForInputNumberOptions(
        val questionEntityState: QuestionEntityState?,
        val optionItemEntityList: List<OptionItemEntity>,
        val inputTypeQuestionEntity: List<InputTypeQuestionAnswerEntity>
    )

    data class UpdateConditionQuestionStateForAnsweredQuestions(
        val questionEntityState: QuestionEntityState?,
        val answeredOptionItemEntityList: List<OptionItemEntity>
    )

    object UpdateCalculationTypeQuestionValue

    object UpdateAutoCalculateTypeQuestionValue : QuestionTypeEvent()

    data class RemoveConditionalQuestionValuesForUnselectedOption(
        val questionConditionsDto: ConditionsDto
    )

    data class FormQuestionMarkedWithNone(val questionId: Int, val optionId: Int)
}