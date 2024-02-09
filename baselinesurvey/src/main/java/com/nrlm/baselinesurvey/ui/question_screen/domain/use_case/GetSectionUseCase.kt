package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.question_screen.domain.repository.QuestionScreenRepository

class GetSectionUseCase(
    private val repository: QuestionScreenRepository
) {

    suspend operator fun invoke(sectionId: Int, surveyId: Int, languageId: Int): SectionListItem {
        return repository.getSections(sectionId, surveyId, languageId)
    }

    fun getSelectedLanguage(): Int {
        return repository.getSelectedLanguage()
    }

}
