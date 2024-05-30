package com.sarathi.contentmodule.ui.content_screen.domain.usecase

import android.net.Uri
import com.sarathi.contentmodule.content_downloder.domain.repository.IContentDownloader
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.data.entities.Content
import java.io.File
import javax.inject.Inject

class FetchContentUseCase @Inject constructor(
    private val repository: IContentDownloader, private val downloaderManager: DownloaderManager,
) {
    suspend fun getContentData(): List<Content> {
        return repository.getContentDataFromDb()
    }

    suspend fun getContentValue(contentKey: String): String {
        return repository.getContentValue(contentKey)
    }

    fun getFilePathUri(filePath: String): Uri? {
        return downloaderManager.getFilePathUri(filePath)
    }

    fun getFilePath(filePath: String): File {
        return downloaderManager.getFilePath(filePath)
    }

    suspend fun getLimitedContentData(limit: Int): List<Content> {
        return repository.getLimitedContentData(limit)
    }

    suspend fun getContentCount(): Int {
        return repository.getContentCount()
    }
    fun isFilePathExists(filePath: String): Boolean {
        return downloaderManager.isFilePathExists(filePath)
    }
}