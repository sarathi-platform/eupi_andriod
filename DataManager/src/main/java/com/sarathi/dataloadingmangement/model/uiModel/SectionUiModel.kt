package com.sarathi.dataloadingmangement.model.uiModel

data class SectionUiModel(
    val sectionId: Int,
    val userId: String,
    val surveyId: Int,
    val sectionName: String,
    val sectionOrder: Int,
    val sectionDetails: String,
    val sectionIcon: String,
    val questionSize: Int,
)