package com.nrlm.baselinesurvey.utils

import com.nrlm.baselinesurvey.model.datamodel.Sections

data class SectionState(
    val section: Sections,
    val sectionStatus: SectionStatus
)
