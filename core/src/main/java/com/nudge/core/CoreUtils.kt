package com.nudge.core

import com.facebook.network.connectionclass.ConnectionQuality
import com.google.gson.Gson
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
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


inline fun <reified T : Any> T.json(): String = Gson().toJson(this, T::class.java)

fun String.getSizeInLong() = this.toByteArray().size.toLong()

fun List<Events>.getEventDependencyEntityListFromEvents(dependentEvents: Events): List<EventDependencyEntity> {
    val eventDependencyList = mutableListOf<EventDependencyEntity>()
    this.forEach { dependsOnEvent ->
        eventDependencyList.add(EventDependencyEntity(dependentEvents.id, dependsOnEvent.id))
    }
    return eventDependencyList
}

fun getBatchSize(connectionQuality: ConnectionQuality): Int {
    return when (connectionQuality) {
        ConnectionQuality.EXCELLENT -> return 20
        ConnectionQuality.GOOD -> return 15
        ConnectionQuality.MODERATE -> return 10
        ConnectionQuality.POOR -> 5
        ConnectionQuality.UNKNOWN -> -1
    }
}