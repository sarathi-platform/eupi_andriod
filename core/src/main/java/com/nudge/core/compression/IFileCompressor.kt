package com.nudge.core.compression

import android.content.Context

interface IFileCompressor {

   suspend fun compressBackupFiles(context: Context,mobileNo:String)
    fun getCompressionType():String
}