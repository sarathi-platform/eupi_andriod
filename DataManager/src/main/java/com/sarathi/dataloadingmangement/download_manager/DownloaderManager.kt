package com.sarathi.dataloadingmangement.download_manager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DownloaderManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    val currentDownloadingId = mutableStateOf(-1)
    val _downloadStatus = MutableStateFlow<Map<Int, DownloadStatus>>(mapOf())
    val initialPosition = mutableStateMapOf<Int, Float>()

    fun downloadFile(url: String, title: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setDescription("Downloading")
            .setMimeType(getMimeType(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DCIM,
                getFileNameFromURL(url)
            )
        return downloadManager.enqueue(request)
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getFileNameFromURL(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1, url.length)
    }

    fun isFilePathExists(filePath: String): Boolean {
        val fileName = getFileNameFromURL(filePath)
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}").exists()
    }
    fun getFilePath(filePath: String): File {
        val fileName = getFileNameFromURL(filePath)
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}/${fileName}")
    }

    @SuppressLint("Range")
    fun monitorDownloadStatus(
        downloadId: Long,
        id: Int,
        downloadManager: DownloadManager
    ) {
        var progress = 0
        var isDownloadFinished = false
        var status: DownloadStatus = DownloadStatus.DOWNLOADING
        GlobalScope.launch {
            while (!isDownloadFinished) {
                val cursor: Cursor? =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                cursor?.let {
                    if (cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_RUNNING -> {
                                val totalBytes: Long =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                if (totalBytes > 0) {
                                    val downloadedBytes: Long =
                                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                    progress = (downloadedBytes * 100 / totalBytes).toInt()
                                }
                                status = DownloadStatus.DOWNLOADING
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }

                            DownloadManager.STATUS_SUCCESSFUL -> {
                                progress = 100
                                isDownloadFinished = true
                                status = DownloadStatus.DOWNLOADED
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }

                            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                                status = DownloadStatus.DOWNLOAD_PAUSED
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }

                            DownloadManager.STATUS_FAILED -> {
                                isDownloadFinished = true
                                status = DownloadStatus.UNAVAILABLE
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }
                        }
                    }
                    cursor.close()
                }
                val downloadPercentage =
                    if (status == DownloadStatus.UNAVAILABLE) 0F else progress.toFloat()
                initialPosition[id] = downloadPercentage
                currentDownloadingId.value = -1
            }
        }
    }

    fun downloadItem(url: String) {
        if (!isFilePathExists(url)) {
            downloadFile(
                url,
                "Downloading files..."
            )
        }
    }

    fun getFilePathUri(filePath: String): Uri? {
        val isFilePathExists = isFilePathExists(filePath)
        return if (isFilePathExists) Uri.fromFile(getFilePath(filePath)) else null
    }
}
