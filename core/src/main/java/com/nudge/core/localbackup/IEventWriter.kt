package com.nudge.core.localbackup

import android.content.Context
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

interface IEventWriter {
suspend fun addEvent(context: Context, event:String)

suspend fun getEventWriteType(): EventWriterName



}