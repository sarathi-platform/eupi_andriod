package com.nudge.core

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.facebook.network.connectionclass.ConnectionQuality
import com.google.gson.Gson
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toDate(dateFormat: Long = System.currentTimeMillis(), timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val dateTime = Date(this)
    val parser = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(parser.format(dateTime))!!
}

fun Long.toDateInMMDDYYFormat(
    dateFormat: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): String {
    val dateTime = Date(this)
    val parser = SimpleDateFormat("MM_dd_yyyy_MM_HH_mm_ss", Locale.getDefault())
    return parser.format(dateTime)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun Long.toTimeDateString(): String {
    val dateTime = Date(this)
    val format = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    return format.format(dateTime)
}


inline fun <reified T : Any> T.json(): String = Gson().toJson(this, T::class.java)

fun String.getSizeInLong() = this.toByteArray().size.toLong()

fun List<Events>.getEventDependencyEntityListFromEvents(dependentEvents: Events): List<EventDependencyEntity> {
    val eventDependencyList = mutableListOf<EventDependencyEntity>()
    this.forEach { dependsOnEvent ->
        eventDependencyList.add(EventDependencyEntity(dependentEvents.id, dependsOnEvent.id))
    }
    return eventDependencyList
}

fun getBatchSize(connectionQuality: ConnectionQuality): Int {
    return when (connectionQuality) {
        ConnectionQuality.EXCELLENT -> return 20
        ConnectionQuality.GOOD -> return 15
        ConnectionQuality.MODERATE -> return 10
        ConnectionQuality.POOR -> 5
        ConnectionQuality.UNKNOWN -> -1
    }
}


fun getUriUsingDisplayName(context: Context, oldName: String): Uri? {

    val extVolumeUri: Uri =
        // Use MediaStore API for Android 10 and higher

        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)


    // query for the file
    val cursor: Cursor? = context.contentResolver.query(
        extVolumeUri,
        null,
        MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.MIME_TYPE + " = ?",
        arrayOf(oldName, "text/plain"),
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
                Log.d("FileWriter", "${displayName} : ${oldName} ")
                if (displayName == oldName) {
                    val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                    if (idIndex > -1) {
                        val id = cursor.getLong(idIndex)
                        fileUri = ContentUris.withAppendedId(extVolumeUri, id)
                    }
                }
            }
        }

        cursor.close()

    }
    return fileUri;
}

fun renameFile(context: Context, oldName: String, newName: String, mobileNumber: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentResolver: ContentResolver = context.contentResolver
        val oldFileUri: Uri? = getUriUsingDisplayName(context, oldName)
        if (oldFileUri == null) {
            return false
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
        }

        val parentUri: Uri = MediaStore.Files.getContentUri("external")
        var newFileUri: Uri? = null

        // Update the file name in MediaStore
        val rowsAffected = contentResolver.update(oldFileUri, values, null, null)

        if (rowsAffected > 0) {
            // Retrieve the ID of the renamed file
            val fileId = ContentUris.parseId(oldFileUri)
            newFileUri = ContentUris.withAppendedId(parentUri, fileId)
        }

        // Optionally, you can handle the renaming success or failure
        if (newFileUri != null) {
            return true
            // Renaming succeeded
            // You can notify the user or take further action if needed
        } else {
            return false
            // Renaming failed
            // You can notify the user or take further action if needed
        }
    } else {
        val fileDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$SARATHI_DIRECTORY_NAME/$mobileNumber"
        )
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs()
        }
        val oldFile = File(fileDirectory, oldName)
        val newFile = File(fileDirectory, newName)
        return oldFile.renameTo(newFile)


    }


}

fun getDefaultBackUpFileName(mobileNo: String): String {
    return LOCAL_BACKUP_FILE_NAME + "_" + mobileNo + "_" + System.currentTimeMillis()
        .toDateInMMDDYYFormat()
}

fun getDefaultImageBackUpFileName(mobileNo: String): String {
    return LOCAL_BACKUP__IMAGE_FILE_NAME + "_" + mobileNo + "_" + System.currentTimeMillis()
        .toDateInMMDDYYFormat()
}



