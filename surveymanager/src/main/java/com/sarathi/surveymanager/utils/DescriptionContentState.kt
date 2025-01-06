package com.sarathi.surveymanager.utils

import com.sarathi.dataloadingmangement.model.survey.response.ContentList

data class DescriptionContentState(
    val contentDescription: List<ContentList?> = emptyList()
)
