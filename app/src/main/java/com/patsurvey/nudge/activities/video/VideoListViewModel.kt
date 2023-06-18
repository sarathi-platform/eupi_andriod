package com.patsurvey.nudge.activities.video

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.download.FileType
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
import com.patsurvey.nudge.utils.DownloadStatus
import com.patsurvey.nudge.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    val trainingVideoDao: TrainingVideoDao
) : BaseViewModel() {

    override fun onServerError(error: ErrorModel?) {

    }
//
//    val initialPosition = mutableStateOf(mutableMapOf<Int, Float>())
//
//    val currentDownloadingId = mutableStateOf(-1)

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    val initialPosition = mutableStateOf(mutableMapOf<Int, Float>())

    val currentDownloadingId = mutableStateOf(-1)

    private val _trainingVideos = MutableStateFlow(listOf<TrainingVideoEntity>())
    val trainingVideos: StateFlow<List<TrainingVideoEntity>> get() = _trainingVideos


    var filterdList by mutableStateOf(listOf<TrainingVideoEntity>())
        private set

//    val _downloadStauts = MutableStateFlow<Map<Int, DownloadStatus>>(mapOf())
//    val downloadStauts: StateFlow<Map<Int, DownloadStatus>> get() = _downloadStauts

    fun getVideoList(context: MainActivity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val mTrainingVideos = trainingVideoDao.getVideoList()
            _trainingVideos.emit(mTrainingVideos)
            context.downloader?.init(mTrainingVideos)
            filterdList = trainingVideos.value
        }
    }

    fun downloadItem(context: Context, videoItem: TrainingVideoEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (!getVideoPath(context, videoItem.id).exists()) {
                    val localDownloader = (context as MainActivity).downloader
                    val downloadManager = context.getSystemService(DownloadManager::class.java)
                    val downloadId = localDownloader?.downloadFile(videoItem, FileType.VIDEO)
                    localDownloader?.currentDownloadingId?.value = videoItem.id
                    if (downloadId != null) {
                        context.downloader?.monitorDownloadStatus(
                            context = context,
                            downloadId = downloadId,
                            id = videoItem.id,
                            downloadManager = downloadManager
                        )
                    }

                    _trainingVideos.value[_trainingVideos.value.map { it.id }
                        .indexOf(videoItem.id)].isDownload = DownloadStatus.DOWNLOADED.value
                    trainingVideoDao.setVideoAsDownloaded(videoItem.id)
                }
            } catch (ex: Exception) {
                Log.e("VideoListViewModel", "downloadItem exception", ex)
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
                val downloaderStatus = (context as MainActivity).downloader
                downloaderStatus?._downloadStatus?.value =
                    downloaderStatus?._downloadStatus?.value?.toMutableMap()
                        ?.also { it[videoItem.id] = DownloadStatus.UNAVAILABLE }!!
                showToast(context, "Video Deleted")
            }
        }
    }
}