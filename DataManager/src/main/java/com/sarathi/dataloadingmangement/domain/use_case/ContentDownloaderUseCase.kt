package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.CoreDispatchers
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.download_manager.FileType
import com.sarathi.dataloadingmangement.repository.IContentDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContentDownloaderUseCase @Inject constructor(
    private val repository: IContentDownloader,
    private val downloaderManager: DownloaderManager,
) {
    suspend fun contentDownloader() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getContentDataFromDb().forEach { content ->
                    when (content.contentType.uppercase().replace("/", "_")) {
                        FileType.VIDEO.name,
                        FileType.IMAGE.name,
                        FileType.IMAGE_PNG.name,
                        FileType.FILE.name,
                        FileType.AUDIO.name -> {
                            downloaderManager.downloadItem(content.contentValue)
                        }
                    }
                }
            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = "ContentDownloader",
                    msg = "downloadItem exception${ex.stackTrace}",
                )
            }
        }
    }

    suspend fun surveyRelateContentDownlaod() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getDidiImagesUrl().forEach {
                    downloaderManager.downloadItem(url = it)

                }

            } catch (ex: Exception) {
                CoreLogger.e(tag = "ContentDownloader", msg = "downloadItem exception", ex = ex)
            }
        }
    }

    suspend fun livelihoodContentDownload() {
        try {
            repository.getLivelihoodContentData().forEach { livelihood ->
                livelihood.image?.let {
                    if (it.isNotEmpty()) {
                        downloaderManager.downloadItem(url = it)
                    }
                }
            }
        } catch (ex: Exception) {
            CoreLogger.e(tag = "ContentDownloader", msg = "downloadItem exception", ex = ex)
        }
    }

    suspend fun didiImagesForSmallGroupDownload() {
        CoreDispatchers.ioCoroutineScope {
            try {
                repository.getDidiImageUrlForSmallGroup().forEach {
                    downloaderManager.downloadItem(url = it)
                }
            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = "ContentDownloader",
                    msg = "didiImagesForSmallGroupDownload, exception -> ${ex.message}",
                    ex = ex,
                    stackTrace = true
                )
            }
        }
    }

}