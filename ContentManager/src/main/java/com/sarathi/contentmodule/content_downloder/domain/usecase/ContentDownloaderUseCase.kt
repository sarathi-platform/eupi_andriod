package com.sarathi.contentmodule.content_downloder.domain.usecase

import android.util.Log
import com.sarathi.contentmodule.content_downloder.domain.repository.IContentDownloader
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.contentmodule.download_manager.FileType
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