package com.nudge.core.model.request

data class EventConsumerRequest(
    val endDate: String,
    val mobile: String,
    val requestId: String,
    val startDate: String
)