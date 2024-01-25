package com.nudge.core.localbackup

import android.content.Context
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

interface  IEventWriterFactory{
    fun  createEventWriter(context: Context,events: Events,eventWriterName: EventWriterName):IEventWriter
}
class EventWriterFactory:IEventWriterFactory {
    override fun createEventWriter(
        context: Context,
        events: Events,
        eventWriterName: EventWriterName
    ): IEventWriter {

       return when(eventWriterName)
        {
          EventWriterName.JSON_EVENT_WRITER ->   JsonEventWriter(context)
           EventWriterName.LOG_EVENT_WRITER -> LogEventWriter(context)
       }
    }
}