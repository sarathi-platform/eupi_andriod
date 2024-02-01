package com.nudge.syncmanager

import com.nudge.core.database.entities.Events


sealed class TestEvent {
    data class SampleEvent(val events: Events)

}