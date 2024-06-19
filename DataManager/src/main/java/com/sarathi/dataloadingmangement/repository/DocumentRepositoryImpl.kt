package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.DocumentDao
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IDocumentRepository {
    override suspend fun saveDocumentToDB(referenceId: String, documentValue: String) {
        documentDao.insertDocumentData(
            DocumentEntity.getDocumentEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                referenceId = referenceId,
                documentValue = documentValue,
                documentType = "FORM_E",
            )
        )
    }

    override suspend fun deleteDocument(referenceId: String): Int {
        return documentDao.deleteDocument(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            documentReferenceId = referenceId
        )
    }

    override suspend fun getDocumentData(): List<DocumentEntity> {
        return documentDao.getDocumentSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
        )
    }
}