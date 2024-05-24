package com.sarathi.contentmodule.ui.content_screen.domain.usecase

import com.sarathi.contentmodule.content_downloder.domain.repository.IContentDownloader
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.data.entities.Content
import javax.inject.Inject

class FetchContentUseCase @Inject constructor(
    private val repository: IContentDownloader, private val downloaderManager: DownloaderManager,
) {
    suspend fun getContentData(): List<Content> {
        return repository.getContentDataFromDb()
    }

    fun isFilePathExists(filePath: String): Boolean {
        return downloaderManager.isFilePathExists(filePath)
    }
}