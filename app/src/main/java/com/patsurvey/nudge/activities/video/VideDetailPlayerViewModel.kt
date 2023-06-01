package com.patsurvey.nudge.activities.video

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.TrainingVideoEntity
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.DownloadStatus
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
class VideDetailPlayerViewModel @Inject constructor(
    val trainingVideoDao: TrainingVideoDao
): BaseViewModel() {

    var showLoader = mutableStateOf(true)
    val mediaItem = mutableStateOf(MediaItem.EMPTY)

    private val _trainingVideo = MutableStateFlow(TrainingVideoEntity(id = -1, "", "", "" ,"", 0))
    val trainingVideo: StateFlow<TrainingVideoEntity> get() = _trainingVideo


    fun getVideoPath(context: Context, id: Int): String {
        Log.d("VideDetailPlayerViewModel", "${context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath}/$id.mp4")
        return "${context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath}/$id.mp4"
    }

    fun getVideoDetails(id: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val videoDetils = trainingVideoDao.getVideo(id)
            withContext(Dispatchers.Main) {
                _trainingVideo.value = videoDetils
                showLoader.value = false
            }
        }
    }

    fun createMediaItem(context: Context, videoItem: TrainingVideoEntity) {
        val mMediaItem = MediaItem.Builder()
            .setUri(if (videoItem.isDownload == DownloadStatus.DOWNLOADED.value) getVideoPath(context, videoItem.id) else videoItem.url
            )
            .setMediaId(videoItem.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(videoItem.title)
                    .build()
            )
            .build()
        mediaItem.value = mMediaItem
    }

    private fun getOutputDirectory(activity: MainActivity): File {
        val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                "${
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES + "/" + activity.resources.getString(
                            R.string.app_name
                        )
                    )
                }"
            )
        } else {
            activity.externalMediaDirs.firstOrNull()?.let {
                File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir
    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

}