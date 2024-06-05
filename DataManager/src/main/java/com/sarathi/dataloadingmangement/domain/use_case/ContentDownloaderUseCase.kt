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
                    when (content.contentType) {
                        FileType.Video.name,
                        FileType.Image.name,
                        FileType.File.name,
                        FileType.Audio.name -> {
                            downloaderManager.downloadItem(content.contentValue)
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("ContentDownloader", "downloadItem exception", ex)
            }
        }
    }


}