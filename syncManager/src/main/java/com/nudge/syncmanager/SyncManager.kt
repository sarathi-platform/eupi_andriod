package com.nudge.syncmanager

import android.util.Log
import com.nudge.communicationModule.EventObserverInterface

class SyncManager: EventObserverInterface {
    init {
        Log.d("SyncManager", " SyncManager:init ")
    }

    override fun <T> onEventCallback(event: T) {
        TODO("Not yet implemented")
    }
}