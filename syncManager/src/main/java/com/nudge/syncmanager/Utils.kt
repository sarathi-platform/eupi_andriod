package com.nudge.syncmanager

import com.nudge.syncmanager.database.entities.Events
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toDate(dateFormat: Long = System.currentTimeMillis(), timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val dateTime = Date(this)
    val parser = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(parser.format(dateTime))!!
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Long.toTimeDateString(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    return format.format(dateTime)
}

fun getSampleEvent(): Events {
    return Events(
        name = "TEST_EVENT",
        type = "notification",
        created_date = System.currentTimeMillis().toDate(),
        modified_date = System.currentTimeMillis().toDate(),
        created_by = "123",
        request_status = "OPEN",
        request_payload = "{status: SUCCESS}",
        response_status = "OPEN",
        reponse_payload = "{status: SUCCESS}",
        retry_count = 0,
        error_message = null,
        metadata = "{metadata: sample data}"
    )
}