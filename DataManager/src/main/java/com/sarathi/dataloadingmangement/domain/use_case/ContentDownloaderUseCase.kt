package com.sarathi.dataloadingmangement.domain.use_case

import android.util.Log
import com.sarathi.contentmodule.download_manager.FileType
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
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
                    when (content.contentType.uppercase()) {
                        FileType.VIDEO.name,
                        FileType.IMAGE.name,
                        FileType.FILE.name,
                        FileType.AUDIO.name -> {
                            downloaderManager.downloadItem(content.contentValue)
                        }

                        else -> {

                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("ContentDownloader", "downloadItem exception", ex)
            }
        }
    }


}