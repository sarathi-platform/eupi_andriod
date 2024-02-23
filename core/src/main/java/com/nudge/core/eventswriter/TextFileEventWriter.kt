package com.nudge.core.eventswriter

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.nudge.core.EVENT_DELIMETER
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.json
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.preference.CoreSharedPrefs
import java.io.File
import java.io.FileWriter


open class TextFileEventWriter : IEventWriter {
    val textMimeType = "text/plain"

    override suspend fun addEvent(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao
    ) {
        writeEventInFile(context, event.toEventRequest().json(), mobileNo, uri)
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.FILE_EVENT_WRITER
    }


    private fun writeEventInFile(context: Context, content: String, mobileNo: String, uri: Uri?) {
        if (TextUtils.isEmpty(content.trim())) return
        val fileNameWithoutExtension = if (uri == null) CoreSharedPrefs.getInstance(context)
            .getBackupFileName(mobileNo) else CoreSharedPrefs.getInstance(context)
            .getImageBackupFileName(mobileNo)
        val fileNameWithExtension = fileNameWithoutExtension + LOCAL_BACKUP_EXTENSION
        val finalContent = content + "\n" + EVENT_DELIMETER + "\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    fileNameWithoutExtension
                )
                put(MediaStore.MediaColumns.MIME_TYPE, textMimeType)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + mobileNo
                )
            }

            val extVolumeUri: Uri =
                // Use MediaStore API for Android 10 and higher

                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)


            // query for the file
            val cursor: Cursor? = context.contentResolver.query(
                extVolumeUri,
                null,
                MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.MIME_TYPE + " = ?",
                arrayOf(fileNameWithExtension, textMimeType),
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
                        Log.d("FileWriter", "${displayName} : ${fileNameWithExtension} ")
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
                    os.write(finalContent.toByteArray())

                    os.close()
                }
            }

            val savedFileCursor: Cursor? = context.contentResolver.query(
                fileUri!!,
                null,
                null, null,
                null
            )
            if (savedFileCursor != null && savedFileCursor.count > 0) {
                while (savedFileCursor.moveToNext()) {
                    val nameIndex =
                        savedFileCursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (nameIndex > -1) {
                        val displayName = savedFileCursor.getString(nameIndex)

                        if (uri != null) {
                            CoreSharedPrefs.getInstance(context)
                                .setImageBackupFileName(displayName.substringBeforeLast("."))
                        } else {
                            CoreSharedPrefs.getInstance(context)
                                .setBackupFileName(displayName.substringBeforeLast("."))
                        }


                        Log.d("File created", displayName)

                    }
                }
            }

        } else {

            try {
                val fileDirectory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "$SARATHI_DIRECTORY_NAME/$mobileNo"
                )
                if (!fileDirectory.exists()) {
                    fileDirectory.mkdirs()
                }
                val filePath = File(fileDirectory, fileNameWithExtension)
                val fw = FileWriter(filePath, true)
                fw.write(finalContent)
                fw.close()
                if (uri != null) {
                    CoreSharedPrefs.getInstance(context)
                        .setImageBackupFileName(fileNameWithoutExtension)
                } else {
                    CoreSharedPrefs.getInstance(context)
                        .setBackupFileName(fileNameWithoutExtension)
                }
            } catch (exception: Exception) {
                throw exception
            }

        }
    }
}


