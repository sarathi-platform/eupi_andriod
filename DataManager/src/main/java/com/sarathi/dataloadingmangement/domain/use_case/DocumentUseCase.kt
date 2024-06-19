package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.repository.DocumentRepositoryImpl
import javax.inject.Inject

class DocumentUseCase @Inject constructor(
    private val repository: DocumentRepositoryImpl,
    private val downloaderManager: DownloaderManager
) {

    suspend fun saveDocumentToDB(
        referenceId: String,
        documentValue: String
    ) {
        return repository.saveDocumentToDB(
            referenceId = referenceId,
            documentValue = documentValue
        )
    }

    suspend fun deleteDocument(
        referenceId: String,
    ): Int {
        return repository.deleteDocument(
            referenceId = referenceId
        )
    }

    suspend fun getDocumentData(): List<DocumentEntity> {
        return repository.getDocumentData()
    }

    fun getFilePathUri(filePath: String): Uri? {
        return downloaderManager.getFilePathUri(filePath)
    }


}
