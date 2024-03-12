package com.nudge.core.compression

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.nudge.core.SARATHI_DIRECTORY_NAME

interface IFileCompressor {

    suspend fun compressBackupFiles(context: Context, mobileNo: String): Uri?
    suspend fun compressBackupImages(
        context: Context,
        mobileNo: String,
        filePath: String = Environment.DIRECTORY_PICTURES + SARATHI_DIRECTORY_NAME + "/" + mobileNo
    ): Uri?

    fun getCompressionType(): String
}