package com.nudge.core.localbackup

import android.content.Context

class JsonEventWriter(context:Context):FileEventWriter(context) {

    override fun addEvent(event: String) {
        

        super.addEvent(event)
    }
}