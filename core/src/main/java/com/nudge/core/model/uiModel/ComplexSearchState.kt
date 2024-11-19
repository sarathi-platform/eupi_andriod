package com.nudge.core.model.uiModel

import com.nudge.core.DEFAULT_ID

data class ComplexSearchState(
    val itemId: Int,
    val itemParentId: Int = DEFAULT_ID,
    val itemType: ItemType,
    val sectionName: String,
    val questionTitle: String,
    val isSectionSearchOnly: Boolean = false
)

enum class ItemType {

    Section,
    Question

}