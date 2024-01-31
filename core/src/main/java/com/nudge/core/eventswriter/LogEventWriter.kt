package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.enums.EventWriterName
import com.nudge.core.utils.CoreLogger

class LogEventWriter():IEventWriter {


    override suspend fun addEvent(context: Context, event: String,uri: Uri?) {
        CoreLogger.d(context, EventWriterName.LOG_EVENT_WRITER.name, event)
        if (uri != null)
            CoreLogger.d(context, EventWriterName.LOG_EVENT_WRITER.name, uri.path.toString())
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return  EventWriterName.LOG_EVENT_WRITER
    }
}