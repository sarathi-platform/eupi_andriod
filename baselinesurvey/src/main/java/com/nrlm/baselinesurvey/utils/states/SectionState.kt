package com.nrlm.baselinesurvey.utils.states

import com.nrlm.baselinesurvey.model.datamodel.SectionListItem

data class SectionState(
    val section: SectionListItem,
    val sectionStatus: SectionStatus
)
