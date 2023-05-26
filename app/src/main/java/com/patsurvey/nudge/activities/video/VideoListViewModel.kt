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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    fun getVideoList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val mTrainingVideos = trainingVideoDao.getVideoList()
            _trainingVideos.emit(mTrainingVideos)

            filterdList = trainingVideos.value
        }
    }

    fun downloadItem(context: Context, videoItem: TrainingVideoEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (!getVideoPath(context, videoItem.id).exists()) {
                    val downloadManager =  context.getSystemService(DownloadManager::class.java)
                    val downloadId =
                        AndroidDownloader(context = context).downloadFile(videoItem, FileType.VIDEO)
                    currentDownloadingId.value = videoItem.id
                    monitorDownloadStatus(
                        context = context,
                        downloadId = downloadId,
                        id = videoItem.id,
                        downloadManager = downloadManager
                    )
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
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                progress = 100
                                isDownloadFinished = true
                                status = DownloadStatus.DOWNLOADED
                            }
                            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> {
                                status = DownloadStatus.DOWNLOADING
                            }
                            DownloadManager.STATUS_FAILED -> {
                                isDownloadFinished = true
                                status = DownloadStatus.UNAVAILABLE
                            }
                        }
                    }
                    cursor.close()
                }
                val downloadUrl = getVideoPath(context = context, videoItemId =  id).absoluteFile
                val downloadPercentage =
                    if (status == DownloadStatus.UNAVAILABLE) 0F else progress.toFloat()
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
}