package com.nudge.core.eventswriter

import android.content.Context
import com.nudge.core.enums.EventFormatterName

interface IEventWriterFactory {
    fun createEventWriter(
        context: Context, eventFormatterName: EventFormatterName,
    ): IEventFormatter
}

class EventWriterFactory : IEventWriterFactory {

    override fun createEventWriter(
        context: Context, eventFormatterName: EventFormatterName,
    ): IEventFormatter {

        return when (eventFormatterName) {
            EventFormatterName.JSON_FORMAT_EVENT -> JsonEventWriter(context)
            EventFormatterName.CSV_FORMAT_EVENT -> JsonEventWriter(context)
        }
    }


}