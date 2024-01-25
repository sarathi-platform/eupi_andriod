package com.nudge.core.localbackup

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.nudge.core.LOCAL_BACKUP_DIRECTORY_NAME
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.LOCAL_BACKUP_FILE_NAME
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


object BackupWriter {
//EventWriter
    //LogEventWriter
   //DBEventWriter
    //FileEventWriter
       //JsonEventWriter
       //CSVEventWriter




    fun saveImageToMediaStore(
        context: Context,
        contentImageUri: Uri,
    ) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + LOCAL_BACKUP_DIRECTORY_NAME
        )

        val resolver = context.contentResolver

        // Using the External Content URI for images
        val imageUri: Uri
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val uri = resolver.insert(imageUri, values)
        try {
            if (uri != null) {
                resolver.openOutputStream(uri).use { outputStream ->
                    uriToBitmap(context, contentImageUri)?.compress(
                        Bitmap.CompressFormat.PNG, 100,
                        outputStream!!
                    )
                }
                Log.d("ImageWriteExample", "Image saved to MediaStore.")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("ImageWriteExample", "Error saving image to MediaStore: " + e.message)
        }
    }

    fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
        val contentResolver = context.contentResolver
        var bitmap: Bitmap? = null
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri!!, "r")
            if (parcelFileDescriptor != null) {
                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun writeEventInFile(context: Context, content: String) {
        val fileNameWithExtension = LOCAL_BACKUP_FILE_NAME + LOCAL_BACKUP_EXTENSION

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, LOCAL_BACKUP_FILE_NAME)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + LOCAL_BACKUP_DIRECTORY_NAME
            )
        }

        val extVolumeUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                    if (displayName == fileNameWithExtension) {
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


    fun compressData(context: Context,) {
        val fileNameWithExtension = LOCAL_BACKUP_FILE_NAME + ".zip"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, LOCAL_BACKUP_FILE_NAME)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS
            )
        }

        val extVolumeUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            MediaStore.MediaColumns.RELATIVE_PATH + " = ?",
            arrayOf(  Environment.DIRECTORY_PICTURES + LOCAL_BACKUP_DIRECTORY_NAME+"/"),null
        )

        val fileUris: ArrayList<Pair<String,Uri?>> = ArrayList()

        // if file found
        if (cursor != null && cursor.count > 0) {
            // get URI
            while (cursor.moveToNext()) {
                        val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                        val displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        if (idIndex > -1) {
                            val id = cursor.getLong(idIndex)
                            val displayName = cursor.getString(displayNameIndex)
                            fileUris.add(Pair(first = displayName, second = ContentUris.withAppendedId(extVolumeUri, id)))
                        }


            }

            cursor.close()
        }

        val zipfileUri = context.contentResolver.insert(extVolumeUri, contentValues)

ZipManager.zip(fileUris,zipfileUri,context)


    }

    private fun getBackupDir(): File {

        val path: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val backupDir = File("$path/sarathi"); if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        return backupDir

    }



    private fun getBackupFile(): File {
        return File("${getBackupDir()}/sarathi_log.txt"); }
}