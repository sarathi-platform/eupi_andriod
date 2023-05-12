package com.patsurvey.nudge.intefaces

interface LocalDbListener {
    fun onInsertionSuccess()
    fun onInsertionFailed()
}