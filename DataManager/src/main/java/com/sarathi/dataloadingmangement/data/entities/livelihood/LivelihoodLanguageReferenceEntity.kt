package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_LANGUAGE_TABLE_NAME

@Entity(tableName = LIVELIHOOD_LANGUAGE_TABLE_NAME)
data class LivelihoodLanguageReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "referenceId")
    var referenceId: Int,
    @ColumnInfo(name = "referenceType")
    var referenceType: String,
    @ColumnInfo(name = "languageCode")
    var languageCode: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "userId")
    var userId: String,
) {
    companion object {
        fun getLivelihoodLanguageEntity(
            languageCode: String,
            languageReferenceId: Int,
            name: String,
            uniqueUserIdentifier: String,
            referenceType: String,
            referenceId: Int?
        ): LivelihoodLanguageReferenceEntity {
            return LivelihoodLanguageReferenceEntity(
                id = 0,
                referenceId = referenceId ?: languageReferenceId ?: 0,
                userId = uniqueUserIdentifier,
                languageCode = languageCode ?: BLANK_STRING,
                name = name ?: BLANK_STRING,
                referenceType = referenceType
            )
        }
    }
}
