package com.sarathi.contentmodule.ui.state

import com.sarathi.dataloadingmangement.data.entities.Content

data class DescriptionContentState(
    val descriptionContentType: List<Content> = emptyList(),
)
