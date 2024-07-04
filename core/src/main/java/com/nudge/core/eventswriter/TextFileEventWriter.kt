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
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
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
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao
    ) {
        writeEventInFile(context, event.toEventRequest().json(), mobileNo, uri)
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.FILE_EVENT_WRITER
    }

    override suspend fun addFailedEventIntoFile(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao,
        fileNameWithoutExtension: String
    ) {
        writeEventInFileWithName(
            context = context,
            content = event.toEventRequest().json(),
            mobileNo = mobileNo,
            uri = uri,
            fileNameWithoutExtension = fileNameWithoutExtension
        )
    }


    private fun writeEventInFile(context: Context, content: String, mobileNo: String, uri: Uri?) {
        if (TextUtils.isEmpty(content.trim())) return

        splitFile(context, mobileNo)
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
                if (fileUri == null) {
                    fileUri = context.contentResolver.insert(extVolumeUri, contentValues)
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


    private fun writeEventInFileWithName(
        context: Context,
        content: String,
        mobileNo: String,
        uri: Uri?,
        fileNameWithoutExtension: String
    ) {
        if (content.trim().isEmpty()) return

        val fileNameWithExtension = fileNameWithoutExtension + LOCAL_BACKUP_EXTENSION
        val finalContent = "$content\n$EVENT_DELIMETER\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writeEventForAndroidQAndAbove(
                context, finalContent, fileNameWithExtension, mobileNo, uri
            )
        } else {
            writeEventForBelowAndroidQ(context, finalContent, fileNameWithExtension, mobileNo, uri)
        }
    }

    private fun writeEventForAndroidQAndAbove(
        context: Context,
        finalContent: String,
        fileNameWithExtension: String,
        mobileNo: String,
        uri: Uri?
    ) {
        val extVolumeUri: Uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentValues = createContentValues(fileNameWithExtension, mobileNo)

        var fileUri = queryFileUri(context, extVolumeUri, fileNameWithExtension)

        if (fileUri == null) {
            fileUri = context.contentResolver.insert(extVolumeUri, contentValues)
        }

        fileUri?.let {
            writeToFile(context, it, finalContent)
            updateFileNameInPreferences(context, it, uri)
        }
    }

    private fun createContentValues(fileName: String, mobileNo: String): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, textMimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + mobileNo
            )
        }
    }

    private fun queryFileUri(
        context: Context, extVolumeUri: Uri, fileNameWithExtension: String
    ): Uri? {
        val cursor: Cursor? = context.contentResolver.query(
            extVolumeUri,
            null,
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.MIME_TYPE} = ?",
            arrayOf(fileNameWithExtension, textMimeType),
            null
        )

        var fileUri: Uri? = null

        cursor?.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(MediaStore.MediaColumns._ID)
                if (idIndex > -1) {
                    val id = it.getLong(idIndex)
                    fileUri = ContentUris.withAppendedId(extVolumeUri, id)
                    break
                }
            }
        }

        return fileUri
    }

    private fun writeToFile(context: Context, fileUri: Uri, content: String) {
        context.contentResolver.openOutputStream(fileUri, "wa")?.use { os ->
            os.write(content.toByteArray())
        }
    }

    private fun updateFileNameInPreferences(context: Context, fileUri: Uri, uri: Uri?) {
        val cursor: Cursor? = context.contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex > -1) {
                    val displayName = it.getString(nameIndex)
                    val coreSharedPrefs = CoreSharedPrefs.getInstance(context)
                    if (uri != null) {
                        coreSharedPrefs.setImageBackupFileName(displayName.substringBeforeLast("."))
                    } else {
                        coreSharedPrefs.setBackupFileName(displayName.substringBeforeLast("."))
                    }
                    Log.d("File created", displayName)
                }
            }
        }
    }

    private fun writeEventForBelowAndroidQ(
        context: Context,
        finalContent: String,
        fileNameWithExtension: String,
        mobileNo: String,
        uri: Uri?
    ) {
        try {
            val fileDirectory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "$SARATHI_DIRECTORY_NAME/$mobileNo"
            )
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }
            val filePath = File(fileDirectory, fileNameWithExtension)
            FileWriter(filePath, true).use { fw ->
                fw.write(finalContent)
            }
            val coreSharedPrefs = CoreSharedPrefs.getInstance(context)
            if (uri != null) {
                coreSharedPrefs.setImageBackupFileName(fileNameWithExtension)
            } else {
                coreSharedPrefs.setBackupFileName(fileNameWithExtension)
            }
        } catch (exception: Exception) {
            throw exception
        }
    }
    private fun splitFile(context: Context, mobileNo: String) {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(context)
        if (CoreSharedPrefs.getInstance(context).isFileExported()) {
                coreSharedPrefs
                    .setBackupFileName(getDefaultBackUpFileName(mobileNo))

                coreSharedPrefs
                    .setImageBackupFileName(getDefaultImageBackUpFileName(mobileNo))

            coreSharedPrefs.setFileExported(false)
        }
    }
}


