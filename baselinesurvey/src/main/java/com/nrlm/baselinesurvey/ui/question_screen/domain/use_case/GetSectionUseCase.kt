package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class GetSectionUseCase(
    private val repository: QuestionScreenRepository
) {

    suspend operator fun invoke(sectionId: Int, languageId: Int): SectionListItem {
        return repository.getSections(sectionId, languageId)
    }

    suspend fun getInputTypeQuestionAnswers(surveyId: Int, sectionId: Int, didiId: Int): List<InputTypeQuestionAnswerEntity> {
        return repository.getAllInputTypeQuestionAnswersForDidi(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId
        )
    }

    fun getSelectedLanguage(): Int {
        return repository.getSelectedLanguage()
    }

}
