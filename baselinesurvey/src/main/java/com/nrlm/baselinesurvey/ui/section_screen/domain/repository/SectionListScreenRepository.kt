package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.Sections

interface SectionListScreenRepository {

    fun getSectionsList(languageId: Int): List<SectionListItem>

    fun getSelectedLanguage(): Int
}
