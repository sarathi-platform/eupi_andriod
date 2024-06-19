package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DOCUMENT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.DocumentEntity

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(documentEntity: DocumentEntity): Long


    @Query("select count(*) from  $DOCUMENT_TABLE_NAME  where userId =:userId  and documentReferenceId=:documentReferenceId")
    fun getDocumentData(
        userId: String,
        documentReferenceId: String
    ): Int

    @Transaction
    fun insertDocumentData(documentEntity: DocumentEntity) {
        if (getDocumentData(
                userId = documentEntity.userId ?: BLANK_STRING,
                documentReferenceId = documentEntity.documentReferenceId
            ) == 0
        ) {
            insertDocument(documentEntity)
        }
    }

    @Query("Delete from $DOCUMENT_TABLE_NAME where userId =:userId and  documentReferenceId =:documentReferenceId")
    fun deleteDocument(
        userId: String,
        documentReferenceId: String,
    ): Int

    @Query("select * from $DOCUMENT_TABLE_NAME where userId =:userId order by generateDate DESC")
    suspend fun getDocumentSummaryData(
        userId: String
    ): List<DocumentEntity>

}