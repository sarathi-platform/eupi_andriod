package com.nudge.core.compression

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.nudge.core.SARATHI_DIRECTORY_NAME

interface IFileCompressor {

    suspend fun compressBackupFiles(
        context: Context,
        extraUris: List<Pair<String, Uri?>>,
        mobileNo: String,
        userName: String,
        moduleName:String
    ): Uri?

    suspend fun compressBackupImages(
        context: Context,
        mobileNo: String,
        filePath: String = Environment.DIRECTORY_PICTURES + SARATHI_DIRECTORY_NAME + "/" + mobileNo,
        userName: String
    ): Uri?

    fun getCompressionType(): String

    fun deleteOldFiles(
        context: Context,
        fileNameReference: String,
        folderName: String,
        fileType: String,
        applicationId: String? = null,
        checkInAppDirectory: Boolean = false
    )

    fun getFileUrisFromMediaStore(
        contentResolver: ContentResolver,
        extVolumeUri: Uri,
        filePathToZipped: String
    ): List<Pair<String, Uri?>>
}