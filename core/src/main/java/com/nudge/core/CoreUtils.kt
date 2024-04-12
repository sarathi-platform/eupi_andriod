package com.nudge.core

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.facebook.network.connectionclass.ConnectionQuality
import com.google.gson.Gson
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.utils.CoreLogger
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Long.toDate(dateFormat: Long = System.currentTimeMillis(), timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val dateTime = Date(this)
    val parser = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ENGLISH)
    parser.timeZone = timeZone
    return parser.parse(parser.format(dateTime))!!
}

fun Long.toDateInMMDDYYFormat(
    dateFormat: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): String {
    val dateTime = Date(this)
    val parser = SimpleDateFormat("MM_dd_yyyy_HH_mm_ss_SSS", Locale.ENGLISH)
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
    try {


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

    } catch (exception: Exception) {
        CoreLogger.e(context, "File Rename", exception.message ?: "")
        return false
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

fun compressImage(imageUri: String, activity: Context, name: String): String? {
    var filename: String? = ""
    try {
        val filePath = imageUri /*getRealPathFromURI(imageUri, activity)*/
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas: Canvas
        if (scaledBitmap != null) {
            canvas = Canvas(scaledBitmap)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }
        val exif: ExifInterface
        try {
            exif = filePath?.let { ExifInterface(it) }!!
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
            } else if (orientation == 3) {
                matrix.postRotate(180F)
            } else if (orientation == 8) {
                matrix.postRotate(270F)
            }
            if (scaledBitmap != null) {
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    0,
                    scaledBitmap.width,
                    scaledBitmap.height,
                    matrix,
                    true
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val out: FileOutputStream
        filename = name
        try {
            val path = File(
                "${activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}",
                filename
            ).absolutePath
            out = FileOutputStream(path)
            val success = scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            return if (success == true) {
                path
            } else BLANK_STRING
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return BLANK_STRING
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}

