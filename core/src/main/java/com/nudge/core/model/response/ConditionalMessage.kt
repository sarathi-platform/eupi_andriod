package com.nudge.core.model.response

data class ConditionalMessage(
    val condition: String,
    val languageList: List<ConditionalMessageLanguage>
)
