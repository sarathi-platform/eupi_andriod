package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository

class GetSectionListUseCase(
    private val sectionListScreenRepository: SectionListScreenRepository
) {

    operator fun invoke(): List<Sections>{
        return sectionListScreenRepository.getSectionsList()
    }

}
