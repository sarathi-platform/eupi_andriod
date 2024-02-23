package com.nudge.core.compression

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.ZIP_MIME_TYPE
import java.io.File

class ZipFileCompression : IFileCompressor {
    private val extension = ".zip"
    override suspend fun compressBackupFiles(context: Context, mobileNo: String): Uri? {

        val zipFileName = "${mobileNo}_sarathi_${System.currentTimeMillis()}_"

        return compressData(
            context,
            zipFileName + "file",
            Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + mobileNo
        );
    }

    override suspend fun compressBackupImages(context: Context, mobileNo: String): Uri? {
        val zipFileName = "${mobileNo}_sarathi_${System.currentTimeMillis()}_"
        return compressData(
            context,
            zipFileName + "image",
            Environment.DIRECTORY_PICTURES + SARATHI_DIRECTORY_NAME + "/" + mobileNo
        )
    }

    override fun getCompressionType(): String {
        return "ZIP"
    }


    private fun compressData(
        context: Context,
        zipFileName: String,
        filePathToZipped: String
    ): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, zipFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, ZIP_MIME_TYPE)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS+ SARATHI_DIRECTORY_NAME
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

            val zipFileUri = context.contentResolver.insert(extVolumeUri, contentValues)

            ZipManager.zip(fileUris, zipFileUri, context)

            return zipFileUri;
        } else {
            try {

                val commonFilePath: File = Environment.getExternalStoragePublicDirectory("")

                val zippedFileDirectoryPath =
                    File(commonFilePath.path + "/" + Environment.DIRECTORY_DOCUMENTS+ SARATHI_DIRECTORY_NAME)

                if (!zippedFileDirectoryPath.exists()) {
                    zippedFileDirectoryPath.mkdirs()
                }
                val zippedFilePath = File(zippedFileDirectoryPath, zipFileName + extension)


                val directoryPathToBeZipped = File(commonFilePath.path + "/" + filePathToZipped)
                val s: List<Pair<String, Uri>>? = directoryPathToBeZipped.listFiles()?.map {
                    Pair(it.name, it.toUri());
                }
                if (s != null) {
                    ZipManager.zip(context = context, files = s, zipFile = zippedFilePath.toUri())
                }
                return zippedFilePath.toUri();
            } catch (e: Exception) {
                e.printStackTrace()

            }
            return null;
        }

    }
}