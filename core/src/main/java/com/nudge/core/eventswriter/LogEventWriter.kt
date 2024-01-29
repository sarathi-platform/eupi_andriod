package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.enums.EventWriterName

class LogEventWriter():IEventWriter {


    override suspend fun addEvent(context: Context, event: String,uri: Uri?) {

    }

    override suspend fun getEventWriteType(): EventWriterName {
        return  EventWriterName.LOG_EVENT_WRITER
    }
}