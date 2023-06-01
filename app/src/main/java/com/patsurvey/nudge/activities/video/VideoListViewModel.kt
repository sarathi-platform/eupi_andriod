package com.patsurvey.nudge.activities.video

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.download.AndroidDownloader
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    val trainingVideoDao: TrainingVideoDao
) : BaseViewModel() {


    override fun onServerError(error: ErrorModel?) {

    }

    val initialPosition = mutableStateOf(0f)

    val currentDownloadingId = mutableStateOf(-1)

    private val _trainingVideos = MutableStateFlow(listOf<TrainingVideoEntity>())
    val trainingVideos: StateFlow<List<TrainingVideoEntity>> get() = _trainingVideos


    var filterdList by mutableStateOf(listOf<TrainingVideoEntity>())
        private set

    val _downloadStauts = MutableStateFlow<Map<Int, DownloadStatus>>(mapOf())
    val downloadStauts: StateFlow<Map<Int, DownloadStatus>> get() = _downloadStauts

    fun getVideoList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val mTrainingVideos = trainingVideoDao.getVideoList()
            _trainingVideos.emit(mTrainingVideos)
            mTrainingVideos.forEach { videoEntity ->
                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                    it[videoEntity.id] = DownloadStatus.fromInt(videoEntity.isDownload)
                }
            }
            filterdList = trainingVideos.value
        }
    }

    fun downloadItem(context: Context, videoItem: TrainingVideoEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (!getVideoPath(context, videoItem.id).exists()) {
                    val downloadManager = context.getSystemService(DownloadManager::class.java)
                    val downloadId =
                        AndroidDownloader(context = context).downloadFile(videoItem, FileType.VIDEO)
                    currentDownloadingId.value = videoItem.id
                    monitorDownloadStatus(
                        context = context,
                        downloadId = downloadId,
                        id = videoItem.id,
                        downloadManager = downloadManager
                    )
                    _trainingVideos.value[_trainingVideos.value.map { it.id }
                        .indexOf(videoItem.id)].isDownload = DownloadStatus.DOWNLOADED.value
                    trainingVideoDao.setVideoAsDownloaded(videoItem.id)
                }
            } catch (ex: Exception) {
                Log.e("VideoListViewModel", "downloadItem exception", ex)
            }
        }

    }

    @SuppressLint("Range")
    private suspend fun monitorDownloadStatus(
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
                                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                progress = 100
                                isDownloadFinished = true
                                status = DownloadStatus.DOWNLOADED
                                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }
                            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                                status = DownloadStatus.DOWNLOAD_PAUSED
                                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }
                            DownloadManager.STATUS_FAILED -> {
                                isDownloadFinished = true
                                status = DownloadStatus.UNAVAILABLE
                                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                                    it[id] = status
                                }
                            }
                        }
                    }
                    cursor.close()
                }
                val downloadUrl = getVideoPath(context = context, videoItemId = id).absoluteFile
                val downloadPercentage = if (status == DownloadStatus.UNAVAILABLE) 0F else progress.toFloat()
                initialPosition.value = downloadPercentage
                currentDownloadingId.value = -1
            }

        }
    }

    fun getVideoPath(context: Context, videoItemId: Int): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath}/${videoItemId}.mp4")
    }

    fun performQuery(query: String) {
        filterdList = if (query.isNotEmpty()) {
            val mFilteredList = ArrayList<TrainingVideoEntity>()
            trainingVideos.value.forEach { videos ->
                if (videos.title.lowercase().contains(query.lowercase())) {
                    mFilteredList.add(videos)
                }
            }
            mFilteredList
        } else {
            trainingVideos.value
        }
    }

    fun removeDownload(context: Context, videoItem: TrainingVideoEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val videoFile = getVideoPath(context, videoItem.id)
            videoFile.delete()
            trainingVideoDao.updateVideoDownloadStatus(
                id = videoItem.id,
                DownloadStatus.UNAVAILABLE.value
            )
            withContext(Dispatchers.Main) {
                _trainingVideos.value[_trainingVideos.value.map { it.id }
                    .indexOf(videoItem.id)].isDownload = DownloadStatus.UNAVAILABLE.value
                _downloadStauts.value = _downloadStauts.value.toMutableMap().also {
                    it[videoItem.id] = DownloadStatus.UNAVAILABLE
                }
                showToast(context, "Video Deleted")
            }
        }
    }
}