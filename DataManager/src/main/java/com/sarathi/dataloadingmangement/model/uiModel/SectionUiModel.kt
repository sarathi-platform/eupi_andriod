package com.sarathi.dataloadingmangement.model.uiModel

import com.sarathi.dataloadingmangement.model.survey.response.ContentList

data class SectionUiModel(
    val sectionId: Int,
    val userId: String,
    val surveyId: Int,
    val sectionName: String,
    val sectionOrder: Int,
    val sectionDetails: String,
    val sectionIcon: String,
    val questionSize: Int,
    val contentEntities: List<ContentList> = listOf()
)