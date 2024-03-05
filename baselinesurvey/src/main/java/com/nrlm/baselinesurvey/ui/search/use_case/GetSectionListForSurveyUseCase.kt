package com.nrlm.baselinesurvey.ui.search.use_case

import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository

class GetSectionListForSurveyUseCase(
    private val sectionListScreenRepository: SectionListScreenRepository
) {

    operator fun invoke(surveyId: Int, languageId: Int): List<SectionListItem> {
        return sectionListScreenRepository.getSectionListForSurvey(surveyId, languageId)
    }

    fun getSelectedLanguage(): Int {
        return sectionListScreenRepository.getSelectedLanguage()
    }

}