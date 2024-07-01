package com.nudge.core

interface CoreObserverInterface {

    fun updateMissionActivityStatusOnGrantInit(onSuccess: (isSuccess: Boolean) -> Unit)

}