package com.nudge.core.preference

interface CorePrefRepo {

    fun getBackupFileName(mobileNo: String): String
    fun setBackupFileName(fileName: String)

    fun getImageBackupFileName(mobileNo: String): String
    fun setImageBackupFileName(fileName: String)
    fun isFileExported(): Boolean
    fun setFileExported(isExported: Boolean)

    fun getUserId():String
    fun getMobileNumber():String
}