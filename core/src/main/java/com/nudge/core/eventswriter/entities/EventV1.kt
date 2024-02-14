package com.nudge.core.eventswriter.entities

import com.nudge.core.toDate


data class EventV1(
    val eventTopic: String,
    val payload: String,
    val createdDate: String = System.currentTimeMillis().toDate().toString(),
    val mobileNumber: String
)