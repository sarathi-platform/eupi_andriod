package com.nudge.core.preference

interface CorePrefRepo {

    fun getBackupFileName(): String
    fun setBackupFileName(fileName: String)
}