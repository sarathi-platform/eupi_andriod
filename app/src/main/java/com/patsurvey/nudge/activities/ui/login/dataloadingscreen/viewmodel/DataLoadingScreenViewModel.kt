package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.toTimeDateString
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.FetchDataUseCase
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.util.LoaderState
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.isFilePathExists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataLoadingScreenViewModel @Inject constructor(val fetchDataUseCase: FetchDataUseCase) :
    BaseViewModel() {
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    private var baseCurrentApiCount = 0 // only count api survey count

    override fun onServerError(error: ErrorModel?) {
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
    }

    fun fetchAllData(callBack: () -> Unit) {
        Log.d(
            "invoke",
            "Network Transaction Start " + System.currentTimeMillis().toTimeDateString()
        )
        try {
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                fetchMissionData(fetchDataUseCase) { callBack() }
                fetchContentData(fetchDataUseCase) { callBack() }
            }
        } catch (ex: Exception) {
            loaderView(false)
            callBack()
        }
    }

    private fun fetchMissionData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchContentData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchContentDataFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private suspend fun updateLoaderEvent(callBack: () -> Unit) {
        if (baseCurrentApiCount == 2) {
            withContext(Dispatchers.Main) {
                Log.d(
                    "invoke",
                    "Network Transaction end " + System.currentTimeMillis().toTimeDateString()
                )
                loaderView(false)
                callBack()
            }
        }
    }

    fun loaderView(isLoaderVisible: Boolean) {
        _loaderState.value = _loaderState.value.copy(
            isLoaderVisible = isLoaderVisible
        )
    }

    //    fun downloadFile(context: Context, url: String, title: String): Long {
    suspend fun downloadContentData(context: Context) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val _contentData =
                    fetchDataUseCase.fetchContentDataFromNetworkUseCase.getContentDataFromDB()
                if (_contentData.isNotEmpty()) {
                    _contentData.forEach { content ->
                        if (!isFilePathExists(
                                context = context,
                                filePath = content.contentValue
                            ) && content.contentType == "Video"
                        ) {
                            val localDownloader = (context as MainActivity).downloader
                            val downloadManager =
                                context.getSystemService(DownloadManager::class.java)
                            val downloadId = localDownloader?.downloadFile(
                                context = context,
                                content.contentValue,
                                "Content File..."
                            )
                            if (downloadId != null) {
                                context.downloader?.monitorDownloadStatus(
                                    context = context,
                                    downloadId = downloadId,
                                    id = content.contentId,
                                    downloadManager = downloadManager
                                )
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("DataLoadingScreenViewModel", "downloadItem exception", ex)
            }
        }
    }


}