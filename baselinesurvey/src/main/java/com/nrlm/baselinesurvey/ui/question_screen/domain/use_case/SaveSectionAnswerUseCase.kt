package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class SaveSectionAnswerUseCase(
    private val repository: QuestionScreenRepository
) {
    fun saveSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionsItem>,
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
        optionItems: List<OptionsItem>,
        questionType: String,
        questionSummary: String
    ) {
        repository.updateSectionAnswerForDidi(
            didiId,
            sectionId,
            questionId,
            optionItems,
            questionType,
            questionSummary
        )
    }

    fun isQuestionAlreadyAnswered(didiId: Int, questionId: Int, sectionId: Int): Int {
        return repository.isQuestionAlreadyAnswered(didiId, questionId, sectionId)
    }

    suspend fun saveSectionAnswersToServer(didiId: Int, surveyId: Int) {
        repository.saveSectionAnswersToServer(didiId, surveyId)
    }

}
