package com.nudge.core.compression

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SUFFIX_EVENT_ZIP_FILE
import com.nudge.core.SUFFIX_IMAGE_ZIP_FILE
import com.nudge.core.ZIP_EXTENSION
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.getFirstName
import com.nudge.core.uriFromFile
import com.nudge.core.utils.CoreLogger
import java.io.File

class ZipFileCompression : IFileCompressor {

    private val TAG = ZipFileCompression::class.java.simpleName

    private val extension = ".zip"
    override suspend fun compressBackupFiles(
        context: Context,
        extraUris: List<Pair<String, Uri?>>,
        mobileNo: String,
        userName: String
    ): Uri? {

        val zipFileName =
            "${getFirstName(userName)}_${mobileNo}_Sarathi_${System.currentTimeMillis()}_"

        deleteOldFiles(
            context,
            "${getFirstName(userName)}_${mobileNo}_Sarathi_",
            mobileNo,
            SUFFIX_EVENT_ZIP_FILE,

            )

        return compressData(
            context,
            zipFileName + "file",
            Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + mobileNo,
            extraUris,
            mobileNo
        )
    }

    //TODO need to be remove mobile Number parameter
    override suspend fun compressBackupImages(
        context: Context,
        mobileNo: String,
        filePath: String,
        userName: String
    ): Uri? {
        val zipFileName =
            "${getFirstName(userName)}_${mobileNo}_SARATHI_${System.currentTimeMillis()}_"

        deleteOldFiles(
            context,
            "${getFirstName(userName)}_${mobileNo}_Sarathi_",
            mobileNo,
            SUFFIX_IMAGE_ZIP_FILE
        )

        return compressData(
            context,
            zipFileName + "Image",
            filePath,
            listOf(),
            mobileNo
        )
    }

    override fun getCompressionType(): String {
        return "ZIP"
    }

    override fun deleteOldFiles(
        context: Context,
        fileNameReference: String,
        folderName: String,
        fileType: String,
        applicationId: String?,
        checkInAppDirectory: Boolean
    ) {
        val contentResolver = context.contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            deleteOldFilesFromExternalDirectoryForQ(
                context,
                contentResolver,
                fileNameReference,
                folderName,
                fileType
            )

        } else {

            deleteOldFilesFromExternalDirectory(
                context,
                fileNameReference,
                folderName,
                fileType
            )

        }

        if (checkInAppDirectory && applicationId != null) {

            deleteOldImageZipFilesFromAppDirectory(
                context,
                contentResolver,
                fileNameReference,
                applicationId
            )

        }

    }

    private fun deleteOldFilesFromExternalDirectory(
        context: Context,
        fileNameReference: String,
        folderName: String,
        fileType: String
    ) {
        val commonFilePath: File = Environment.getExternalStoragePublicDirectory("")

        val zippedFileDirectoryPath =
            File(commonFilePath.path + "/" + Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + folderName)

        val files = zippedFileDirectoryPath.listFiles()

        val filteredFiles = files?.filter {
            it.isFile && it.name.contains(
                fileNameReference,
                true
            ) && it.name.contains(ZIP_EXTENSION)
        }

        filteredFiles?.forEach { file ->
            try {
                if (file.exists()) {
                    if (file.delete()) {
                        CoreLogger.d(
                            context,
                            TAG,
                            "deleteOldFiles -> file Deleted :" + file.getPath()
                        );
                    } else {
                        CoreLogger.d(
                            context,
                            TAG,
                            "deleteOldFiles -> file not Deleted :" + file.getPath()
                        );
                    }
                }
            } catch (ex: Exception) {
                CoreLogger.e(
                    context,
                    TAG,
                    "deleteOldFiles -> file: ${file.name}, exception: ${ex.message}",
                    ex,
                    true
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteOldFilesFromExternalDirectoryForQ(
        context: Context,
        contentResolver: ContentResolver?,
        fileNameReference: String,
        folderName: String,
        fileType: String
    ) {

        try {

            contentResolver?.let { cr ->
                val extVolumeUri: Uri =
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val filePathToZipped =
                    Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + folderName

                // query for the file
                val cursor: Cursor? = contentResolver.query(
                    extVolumeUri,
                    null,
                    MediaStore.MediaColumns.RELATIVE_PATH + " = ?",
                    arrayOf("$filePathToZipped/"), null
                )

                val fileUris: ArrayList<Pair<String, Uri?>> = ArrayList()

                // if file found
                if (cursor != null && cursor.count > 0) {
                    // get URI
                    while (cursor.moveToNext()) {
                        val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                        val displayNameIndex =
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        if (idIndex > -1) {
                            val id = cursor.getLong(idIndex)
                            val displayName = cursor.getString(displayNameIndex)
                            fileUris.add(
                                Pair(
                                    first = displayName,
                                    second = ContentUris.withAppendedId(extVolumeUri, id)
                                )
                            )
                        }
                    }

                    cursor.close()
                }

                val filteredUris = fileUris.filter {
                    it.first.contains(
                        fileNameReference,
                        true
                    ) && it.first.contains(ZIP_EXTENSION)
                }


                filteredUris.forEach { file ->
                    try {
                        file.second?.let { uri ->
                            cr.delete(uri, null, null)
                            CoreLogger.d(
                                context,
                                TAG,
                                "deleteOldFiles -> file Deleted :" + file.first
                            );
                        } ?: {
                            CoreLogger.d(
                                context,
                                TAG,
                                "deleteOldFiles -> file not Deleted :" + file.first
                            );
                            throw NullPointerException("File ${file.first} with uri: ${file.second} is null")
                        }
                    } catch (ex: Exception) {
                        CoreLogger.e(
                            context,
                            TAG,
                            "deleteOldFiles -> file: ${file.first}, exception: ${ex.message}",
                            ex,
                            true
                        )
                    }
                }

            } ?: throw NullPointerException("Content resolver is null")

        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "deleteOldFilesFromExternalDirectory -> exception: ${ex.message}",
                ex,
                true
            )
        }

    }

    private fun deleteOldImageZipFilesFromAppDirectory(
        context: Context,
        contentResolver: ContentResolver?,
        fileNameReference: String,
        applicationId: String
    ) {
        val directoryPath =
            context
                .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.path

        try {

            directoryPath?.let { path ->
                val fileList: MutableList<Pair<String, Uri>> = ArrayList()
                val directory = File(path)
                if (directory.exists() && directory.isDirectory) {
                    val files = directory.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (file.isFile) {
                                fileList.add(
                                    Pair(
                                        first = file.name,
                                        uriFromFile(context, file, applicationId)
                                    )
                                )
                            }
                        }
                    }
                }

                val filteredList = fileList.filter {
                    it.first.contains(
                        fileNameReference,
                        true
                    ) && it.first.contains(ZIP_EXTENSION)
                }

                contentResolver?.let { cr ->
                    filteredList.forEach { file ->
                        try {
                            file.second.let { uri ->
                                contentResolver?.delete(uri, null, null)
                                CoreLogger.d(
                                    context,
                                    TAG,
                                    "deleteOldFiles -> file Deleted :" + file.first
                                )
                            }
                        } catch (ex: Exception) {
                            CoreLogger.e(
                                context,
                                TAG,
                                "deleteOldFiles -> file: ${file.first}, exception: ${ex.message}",
                                ex,
                                true
                            )
                        }
                    }
                } ?: throw NullPointerException()

            } ?: throw NullPointerException("Content resolver is null")

        } catch (ex: Exception) {
            CoreLogger.e(
                context,
                TAG,
                "deleteOldImageZipFilesFromAppDirectory -> exception: ${ex.message}",
                ex,
                true
            )
        }

    }


    //TODO: Need to pass Filter data/File Type condition using parameter
    fun compressData(
        context: Context,
        zipFileName: String,
        filePathToZipped: String,
        extraUris: List<Pair<String, Uri?>>,
        folderName: String
    ): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, zipFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, ZIP_MIME_TYPE)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME +"/"+folderName
                )
            }

            val extVolumeUri: Uri =
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            // query for the file
            val cursor: Cursor? = context.contentResolver.query(
                extVolumeUri,
                null,
                MediaStore.MediaColumns.RELATIVE_PATH + " = ?",
                arrayOf("$filePathToZipped/"), null
            )

            val fileUris: ArrayList<Pair<String, Uri?>> = ArrayList()

            // if file found
            if (cursor != null && cursor.count > 0) {
                // get URI
                while (cursor.moveToNext()) {
                    val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                    val displayNameIndex =
                        cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (idIndex > -1) {
                        val id = cursor.getLong(idIndex)
                        val displayName = cursor.getString(displayNameIndex)
                        if(!displayName.contains("zip")) {
                            fileUris.add(
                                Pair(
                                    first = displayName,
                                    second = ContentUris.withAppendedId(extVolumeUri, id)
                                )
                            )
                        }
                    }


                }

                cursor.close()
            }

            val zipFileUri = context.contentResolver.insert(extVolumeUri, contentValues)

            fileUris.addAll(extraUris.filter { !it.first.contains("zip") })
            ZipManager.zip(fileUris, zipFileUri, context)

            return zipFileUri
        } else {
            try {

                val commonFilePath: File = Environment.getExternalStoragePublicDirectory("")

                val zippedFileDirectoryPath =
                    File(commonFilePath.path + "/" + Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME+"/"+folderName)

                if (!zippedFileDirectoryPath.exists()) {
                    zippedFileDirectoryPath.mkdirs()
                }
                val zippedFilePath = File(zippedFileDirectoryPath, zipFileName + extension)


                val directoryPathToBeZipped = File(commonFilePath.path + "/" + filePathToZipped)

                val s: List<Pair<String, Uri>>? = directoryPathToBeZipped.listFiles()?.filter { it.isFile && !it.name.contains("zip") }?.map {
                    Pair(it.name, it.toUri())
                }
                if (s != null) {
                    val filesToBeZipped = ArrayList<Pair<String, Uri?>>()
                    filesToBeZipped.addAll(s)
                    filesToBeZipped.addAll(extraUris.filter { !it.first.contains("zip") })
                    ZipManager.zip(
                        context = context,
                        files = filesToBeZipped,
                        zipFile = zippedFilePath.toUri()
                    )
                }
                return zippedFilePath.toUri();
            } catch (e: Exception) {
                e.printStackTrace()

            }
            return null;
        }

    }

}