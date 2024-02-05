package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.enums.EventWriterName

interface IEventWriter {
suspend fun addEvent(context: Context, event:String,mobileNo:String,uri: Uri?)

suspend fun getEventWriteType(): EventWriterName



}