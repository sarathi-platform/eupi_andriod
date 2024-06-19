package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.DocumentEntity

interface IDocumentRepository {
    suspend fun saveDocumentToDB(referenceId: String, documentValue: String)

    suspend fun deleteDocument(
        referenceId: String,
    ): Int

    suspend fun getDocumentData(): List<DocumentEntity>
}