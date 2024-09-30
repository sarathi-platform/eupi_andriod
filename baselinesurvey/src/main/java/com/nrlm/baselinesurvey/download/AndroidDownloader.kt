package com.nrlm.baselinesurvey.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.os.Environment
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.KEY_HEADER_AUTH
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.download.interfaces.Downloader
import com.nrlm.baselinesurvey.download.utils.DownloadStatus
import com.nrlm.baselinesurvey.download.utils.FileType
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.getAuthImageFileNameFromURL
import com.nrlm.baselinesurvey.utils.getFileNameFromURL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDownloader @Inject constructor(@ApplicationContext private val context: Context) :
    Downloader {

    var job: Job? = null
    val mContext:Context = context
    val _downloadStatus = MutableStateFlow<Map<Int, DownloadStatus>>(mapOf())
    val downloadStatus: StateFlow<Map<Int, DownloadStatus>> get() = _downloadStatus

    val initialPosition = mutableStateMapOf<Int, Float>()

    val currentDownloadingId = mutableStateOf(-1)

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    /*fun init(mTrainingVideos: List<TrainingVideoEntity>) {
        mTrainingVideos.forEach { videoEntity ->
            _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                it[videoEntity.id] = DownloadStatus.fromInt(videoEntity.isDownload)
            }
        }
    }*/

/*
    override fun downloadFile(videoItem: TrainingVideoEntity, fileType: FileType): Long {
        val request = DownloadManager.Request(videoItem.url.toUri())
            .setTitle("Training Videos")
            .setDescription("Downloading")
            .setMimeType(if (fileType == FileType.VIDEO) "video/mp4" else if (fileType == FileType.IMAGE) "image/jpeg" else "application/pdf")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "${videoItem.id}.mp4")
        return downloadManager.enqueue(request)

    }
*/

    override fun downloadImageFile(imageUrl: String, fileType: FileType): Long {
        val request = DownloadManager.Request(imageUrl.toUri())
            .setTitle("Question Images")
            .setDescription("Downloading")
            .setMimeType(if (fileType == FileType.VIDEO) "video/mp4" else if (fileType == FileType.IMAGE) "image/jpeg" else "application/pdf")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DCIM, "${getFileNameFromURL(imageUrl)}")
        return downloadManager.enqueue(request)
    }

    override fun downloadAuthorizedImageFile(imageUrl: String, fileType: FileType, prefBSRepo: PrefBSRepo): Long {
        val request = DownloadManager.Request(imageUrl.toUri())
            .addRequestHeader(
                KEY_HEADER_AUTH,
                "Bearer " + (prefBSRepo.getAccessToken()!!))
            .setTitle("Didi Images")
            .setDescription("Downloading")
            .setMimeType(if (fileType == FileType.VIDEO) "video/mp4" else if (fileType == FileType.IMAGE) "image/jpeg" else "application/pdf")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DCIM, "${getAuthImageFileNameFromURL(imageUrl)}")
        return downloadManager.enqueue(request)
    }

    @SuppressLint("Range")
    fun monitorDownloadStatus(
        context: Context,
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
//                val downloadUrl = getVideoPath(context = context, videoItemId = id).absoluteFile
                val downloadPercentage =
                    if (status == DownloadStatus.UNAVAILABLE) 0F else progress.toFloat()
                initialPosition[id] = downloadPercentage
                currentDownloadingId.value = -1
            }

        }
    }

    @SuppressLint("Range")
    fun checkDownloadStatus(
        downloadId: Long,
        id: Int,
        downloadManager: DownloadManager,
        onDownloadComplete:()->Unit,
        onDownloadFailed:()->Unit,
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
                                onDownloadComplete()
                                BaselineLogger.d("AndroidDownloader", "checkDownloadStatus -> onDownloadComplete = downloadId: $downloadId, id: $id, status: $status, ")
                            }

                            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                                status = DownloadStatus.DOWNLOAD_PAUSED
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                                /*BaselineLogger.d("AndroidDownloader", "checkDownloadStatus -> downloadId: $downloadId, id: $id, status: $status, ")*/
                            }

                            DownloadManager.STATUS_FAILED -> {
                                isDownloadFinished = true
                                status = DownloadStatus.UNAVAILABLE
                                _downloadStatus.value = _downloadStatus.value.toMutableMap().also {
                                    it[id] = status
                                }
                                BaselineLogger.d("AndroidDownloader", "checkDownloadStatus -> onDownloadFailed = downloadId: $downloadId, id: $id, status: $status, ")
                                onDownloadFailed()
                            }
                        }
                    }
                    cursor.close()
                }
//                val downloadUrl = getVideoPath(context = context, videoItemId = id).absoluteFile
                val downloadPercentage =
                    if (status == DownloadStatus.UNAVAILABLE) 0F else progress.toFloat()
                initialPosition[id] = downloadPercentage
                currentDownloadingId.value = -1
            }

        }
    }
}