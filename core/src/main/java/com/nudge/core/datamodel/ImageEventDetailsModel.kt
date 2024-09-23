package com.nudge.core.datamodel

import com.nudge.core.BLANK_STRING
import com.nudge.core.toDate
import java.util.Date
import java.util.UUID

data class ImageEventDetailsModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val created_date: Date = System.currentTimeMillis().toDate(),
    val modified_date: Date,
    val createdBy: String,
    val mobile_number: String,
    val request_payload: String?,
    val status: String,
    var retry_count: Int = 0,
    val error_message: String? = BLANK_STRING,
    val metadata: String?,
    val payloadLocalId: String?,
    val requestId: String? = BLANK_STRING,
    val eventId: String? = BLANK_STRING,
    val imageStatusId: String? = BLANK_STRING,
    val fileName: String? = BLANK_STRING,
    val filePath: String? = BLANK_STRING,
    val isBlobUploaded: Boolean = false
)
