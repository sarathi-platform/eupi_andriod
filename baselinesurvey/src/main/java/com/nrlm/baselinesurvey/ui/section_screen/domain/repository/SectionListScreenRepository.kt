package com.nrlm.baselinesurvey.ui.section_screen.domain.repository

import com.nrlm.baselinesurvey.model.datamodel.Sections

interface SectionListScreenRepository {

    fun getSectionsList(): List<Sections>
}
