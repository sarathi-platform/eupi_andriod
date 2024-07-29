package com.sarathi.contentmodule.ui.contentscreen.domain.usecase

import android.net.Uri
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.repository.IContentDownloader
import java.io.File
import javax.inject.Inject

class FetchContentUseCase @Inject constructor(
    private val repository: IContentDownloader, private val downloaderManager: DownloaderManager,
) {
    suspend fun getContentData(matId: Int, contentCategory: Int): List<Content> {
        val contentKeys = getContentKeys(referenceID = matId, referenceType = contentCategory)

        return repository.getSpecificContentDataFromDb(contentKeys)
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

    suspend fun getLimitedContentData(limit: Int, matId: Int, contentCategory: Int): List<Content> {
        val contentKeys = getContentKeys(referenceID = matId, referenceType = contentCategory)

        return repository.getLimitedContentData(limit, contentKeys)
    }

    suspend fun getContentCount(matId: Int, contentCategory: Int): Int {
        val contentKeys = getContentKeys(referenceID = matId, referenceType = contentCategory)

        return repository.getContentCount(contentKeys)
    }
    fun isFilePathExists(filePath: String): Boolean {
        return downloaderManager.isFilePathExists(filePath)
    }

    suspend fun getContentKeys(referenceID: Int, referenceType: Int): List<String> {
        return repository.getContentKeyFromContentConfig(referenceID, referenceType)
    }
}