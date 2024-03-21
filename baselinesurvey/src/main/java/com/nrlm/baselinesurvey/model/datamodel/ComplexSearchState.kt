package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.ui.Constants.ItemType

data class ComplexSearchState(
    val itemId: Int,
    val itemParentId: Int = DEFAULT_ID,
    val itemType: ItemType,
    val sectionName: String,
    val questionTitle: String,
    val isSectionSearchOnly: Boolean = false
)