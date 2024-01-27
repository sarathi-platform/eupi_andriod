package com.nudge.core.localbackup

import android.content.Context
import com.nudge.core.enums.EventWriterName

class LogEventWriter():IEventWriter {


    override suspend fun addEvent(context: Context, event: String) {

    }

    override suspend fun getEventWriteType(): EventWriterName {
        return  EventWriterName.LOG_EVENT_WRITER
    }
}