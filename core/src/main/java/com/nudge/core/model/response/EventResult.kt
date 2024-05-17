package com.nudge.core.model.response

import com.nudge.core.BLANK_STRING

data class EventResult(
    val eventId: String,
    val message: String?= BLANK_STRING,
    val status: String
)