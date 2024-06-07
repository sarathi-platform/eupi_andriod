package com.nudge.core.eventswriter

import android.content.Context
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.enums.EventFormatterName

interface IEventWriterFactory {
    fun createEventWriter(
        context: Context, eventFormatterName: EventFormatterName,
        eventsDao: EventsDao, eventDependencyDao: EventDependencyDao, eventStatusDao: EventStatusDao
    ): IEventFormatter
}

class EventWriterFactory : IEventWriterFactory {

    override fun createEventWriter(
        context: Context, eventFormatterName: EventFormatterName,
        eventsDao: EventsDao, eventDependencyDao: EventDependencyDao,eventStatusDao: EventStatusDao
    ): IEventFormatter {

        return when (eventFormatterName) {
            EventFormatterName.JSON_FORMAT_EVENT -> JsonEventWriter(
                context = context,
                eventsDao =  eventsDao,
                eventDependencyDao =  eventDependencyDao,
                eventStatusDao = eventStatusDao

            )

            EventFormatterName.CSV_FORMAT_EVENT -> JsonEventWriter(
                context = context,
                eventsDao = eventsDao,
                eventDependencyDao = eventDependencyDao,
                eventStatusDao = eventStatusDao
            )
        }
    }


}