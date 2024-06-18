package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DOCUMENT_TABLE_NAME
import java.util.Date

@Entity(tableName = DOCUMENT_TABLE_NAME)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var generateDate: Date = System.currentTimeMillis().toDate(),
    var documentType: String,
    var documentValue: String,
    var documentReferenceId: String,

    ) {
    companion object {
        fun getDocumentEntity(
            userId: String,
            referenceId: String,
            documentType: String,
            documentValue: String
        ): DocumentEntity {
            return DocumentEntity(
                id = 0,
                documentType = documentType,
                userId = userId,
                documentReferenceId = referenceId,
                documentValue = documentValue,
                generateDate = System.currentTimeMillis().toDate()
            )
        }

    }

}