package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository

class GetSectionListUseCase(
    private val sectionListScreenRepository: SectionListScreenRepository
) {

    operator fun invoke(didiId: Int, surveyId: Int, languageId: Int): List<SectionListItem> {
        return sectionListScreenRepository.getSectionsListForDidi(didiId, surveyId, languageId)
    }

    fun getSelectedLanguage(): Int {
        return sectionListScreenRepository.getSelectedLanguage()
    }

}
