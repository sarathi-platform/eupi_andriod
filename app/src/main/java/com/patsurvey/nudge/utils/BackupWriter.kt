package com.patsurvey.nudge.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.media3.common.MimeTypes
import java.io.File
import java.io.FileOutputStream
import java.net.URL


object BackupWriter {

    fun writeEventInFile(context: Context=NudgeCore.getAppContext(), content: String, fileName: String="Sarathi_backup") {
        var fileNameWithExtension=fileName+".txt"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS+"/Sarathi"
            )
        }

        val extVolumeUri: Uri
        extVolumeUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore API for Android 10 and higher

            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            // For Android 9 and lower, use deprecated method
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        // query for the file
        val cursor: Cursor? = context.contentResolver.query(
            extVolumeUri,
            null,
            MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.MIME_TYPE + " = ?",
            arrayOf(fileNameWithExtension, "text/plain"),
            null
        )

        var fileUri: Uri? = null

        // if file found
        if (cursor != null && cursor.count > 0) {
            // get URI
            while (cursor.moveToNext()) {
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex > -1) {
                    val displayName = cursor.getString(nameIndex)
                    if (displayName ==fileNameWithExtension) {
                        val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                        if (idIndex > -1) {
                            val id = cursor.getLong(idIndex)
                            fileUri = ContentUris.withAppendedId(extVolumeUri, id)
                        }
                    }
                }
            }

            cursor.close()
        } else {
            // insert new file otherwise
            fileUri = context.contentResolver.insert(extVolumeUri, contentValues)
        }

        if (fileUri != null) {
            val os = context.contentResolver.openOutputStream(fileUri, "wa")

            if (os != null) {
                os.write(content.toByteArray())
                os.write("\n".toByteArray())
                os.write("~||~||~||~".toByteArray())
                os.write("\n".toByteArray())
                os.close()
            }
        }

    }


    fun compressData(): String {
        var error = "";
        try {

            val path: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

            val s = getBackupDir().listFiles()?.map {
                it.absolutePath;
            }
            if (s != null) {
                ZipManager.zip(s, path.absolutePath + "/Sarathi_backup.zip")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error = e.message!!

        }
        return error;

    }

    private fun getBackupDir(): File {

        val path: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        var backupDir = File("$path/sarathi");
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        return backupDir;

    }

    private fun getBackupFile(): File {
        return File("${getBackupDir()}/sarathi_log.txt");
    }
}