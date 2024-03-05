package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository

class GetSurvyeDetails(private val repository: SectionListScreenRepository) {
    fun getSurveyeDetails(didiId: Int): SurveyeeEntity {
        return repository.getSurveyeDetails(didiId)
    }
}