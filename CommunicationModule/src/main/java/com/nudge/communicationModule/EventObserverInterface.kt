package com.nudge.communicationModule

interface EventObserverInterface {

    fun <T> onEventCallback(event: T)

}