package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState

class SaveSectionAnswerUseCase(
    private val repository: QuestionScreenRepository
) {
    fun saveSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    ) {
        repository.saveSectionAnswerForDidi(
            didiId,
            sectionId,
            questionId,
            surveyId,
            optionItems,
            questionType,
            questionSummary
        )
    }

    fun updateSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    ) {
        repository.updateSectionAnswerForDidi(
            didiId = didiId,
            sectionId = sectionId,
            questionId = questionId,
            surveyId = surveyId,
            optionItems = optionItems,
            questionType = questionType,
            questionSummary = questionSummary
        )
    }

    fun isQuestionAlreadyAnswered(didiId: Int, questionId: Int, sectionId: Int, surveyId: Int): Int {
        return repository.isQuestionAlreadyAnswered(
            didiId = didiId,
            questionId = questionId,
            sectionId = sectionId,
            surveyId = surveyId
        )
    }

    fun isInputTypeQuestionAlreadyAnswered(surveyId: Int, sectionId: Int, questionId: Int, didiId: Int, optionId: Int): Int {
        return repository.isInputTypeQuestionAlreadyAnswered(surveyId = surveyId, sectionId = sectionId, questionId = questionId, didiId = didiId, optionItemId = optionId)
    }

    suspend fun saveSectionAnswersToServer(didiId: Int, surveyId: Int) {
        repository.saveSectionAnswersToServer(didiId, surveyId)
    }

    suspend fun updateInputTypeQuestionAnswer(surveyId: Int, sectionId: Int, questionId: Int, didiId: Int, optionId: Int, inputValue: String) {
        repository.updateInputTypeQuestionAnswer(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            didiId = didiId,
            optionId = optionId,
            inputValue = inputValue
        )
    }

    suspend fun saveInputTypeQuestionAnswer(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int,
        optionId: Int,
        inputValue: String
    ) {
        repository.saveInputTypeQuestionAnswer(
            surveyId,
            sectionId,
            questionId,
            didiId,
            optionId,
            inputValue
        )
    }

    suspend fun deleteSectionResponseForQuestion(
        question: QuestionEntityState?,
        didiId: Int,
        optionList: List<OptionItemEntity>
    ) {
        when (question?.questionEntity?.type) {
            QuestionType.InputNumber.name -> {
                optionList.forEach {
                    val isQuestionPresentInDb = isInputTypeQuestionAlreadyAnswered(
                        surveyId = question.questionEntity.surveyId,
                        sectionId = question.questionEntity.sectionId,
                        questionId = question.questionId ?: 0,
                        didiId = didiId,
                        it.optionId ?: 0
                    )
                    if (isQuestionPresentInDb > 0) {
                        repository.deleteInputTypeQuestion(
                            surveyId = question.questionEntity.surveyId,
                            sectionId = question.questionEntity.sectionId,
                            questionId = question.questionId ?: 0,
                            didiId = didiId,
                            it.optionId ?: 0
                        )
                    }
                }
            }

            else -> {
                val isQuestionPresentInDb = isQuestionAlreadyAnswered(
                    didiId,
                    question?.questionId ?: 0,
                    question?.questionEntity?.sectionId ?: 0,
                    surveyId = question?.questionEntity?.surveyId ?: 0
                )
                if (isQuestionPresentInDb > 0) {
                    repository.deleteResponseForQuestion(
                        didiId = didiId,
                        questionId = question?.questionId ?: 0,
                        sectionId = question?.questionEntity?.sectionId ?: 0,
                        surveyId = question?.questionEntity?.surveyId ?: 0
                    )
                }
            }
        }
    }

    /*suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String
    ) {
        return repository.updateOptionItemValue(
            surveyId = surveyId,
            sectionId = sectionId,
            questionId = questionId,
            optionId = optionId,
            selectedValue = selectedValue
        )
    }*/


}
