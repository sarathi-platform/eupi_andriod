package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository

class GetSectionProgressForDidiUseCase (
    private val repository: SectionListScreenRepository
) {

    operator fun invoke(didiId: Int, languageId:Int): List<DidiSectionProgressEntity> {
        return repository.getSectionProgressForDidi(didiId, languageId)
    }

//    fun getSectionProgressStatusForDidi(didiId: Int, sectionId: Int, surveyId: Int): DidiSectionProgressEntity

}
