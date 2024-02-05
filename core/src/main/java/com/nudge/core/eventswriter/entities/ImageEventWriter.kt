package com.nudge.core.eventswriter.entities

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventswriter.IEventWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ImageEventWriter : IEventWriter {
    val mimeType = "image/png"
    override suspend fun addEvent(context: Context, event: String, mobileNo: String, uri: Uri?) {
        uri?.let { saveImageToMediaStore(context, it,mobileNo) }
    }


    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.IMAGE_EVENT_WRITER
    }

    private fun saveImageToMediaStore(
        context: Context,
        contentImageUri: Uri,
        mobileNo: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + SARATHI_DIRECTORY_NAME+"/"+mobileNo
            )
            values.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                getFileNameFromURL(contentImageUri.path.toString())
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
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            val fileDirectory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                SARATHI_DIRECTORY_NAME+"/"+mobileNo
            )
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }
            val filePath = File(
                fileDirectory,
                getFileNameFromURL(contentImageUri.path.toString())
            )
            val destURi = filePath.toUri()
            val resolver = context.contentResolver
            try {
                resolver.openOutputStream(destURi).use { outputStream ->
                    uriToBitmap(context, contentImageUri)?.compress(
                        Bitmap.CompressFormat.PNG, 100,
                        outputStream!!
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
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

    private fun getFileNameFromURL(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1, url.length)
    }
}

