package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.DidiSectionProgressEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem

interface SectionListScreenRepository {

    fun getSectionsListForDidi(didiId: Int, surveyId: Int, languageId: Int): List<SectionListItem>

    fun getSelectedLanguage(): Int
    fun getSectionProgressForDidi(
        didiId: Int,
        surveyId: Int,
        languageId: Int
    ): List<DidiSectionProgressEntity>

    fun getSurveyeDetails(didiId: Int): SurveyeeEntity
}
