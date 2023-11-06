package com.nrlm.baselinesurvey.utils

import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.datamodel.Sections

data class SectionState(
    val section: SectionListItem,
    val sectionStatus: SectionStatus
)
