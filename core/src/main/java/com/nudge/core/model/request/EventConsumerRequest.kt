package com.nudge.core.model.request

data class EventConsumerRequest(
    val endDate: String,
    val mobile: String,
    val requestId: List<String?>,
    val startDate: String
)