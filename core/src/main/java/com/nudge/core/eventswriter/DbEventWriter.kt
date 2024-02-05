package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.enums.EventWriterName

class DbEventWrite():IEventWriter {


    override suspend fun addEvent(context: Context, event: String, mobileNo: String, uri: Uri?) {

    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.DB_EVENT_WRITER
    }
}