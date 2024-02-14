package com.nudge.core.compression

import android.content.Context
import android.net.Uri

interface IFileCompressor {

   suspend fun compressBackupFiles(context: Context,mobileNo:String): Uri?
    fun getCompressionType():String
}