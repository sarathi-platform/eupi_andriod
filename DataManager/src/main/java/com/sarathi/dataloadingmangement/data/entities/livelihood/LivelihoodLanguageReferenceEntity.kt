package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.LanguageReference

@Entity(tableName = LIVELIHOOD_LANGUAGE_TABLE_NAME)
data class LivelihoodLanguageReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "id")
    var livelihoodId: Int,
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
            languageReference: LanguageReference,
            uniqueUserIdentifier: String,
            referenceType: String
        ): LivelihoodLanguageReferenceEntity {
            return LivelihoodLanguageReferenceEntity(
                id = 0,
                livelihoodId = languageReference.id ?: 0,
                userId = uniqueUserIdentifier,
                languageCode = languageReference.languageCode ?: BLANK_STRING,
                name = languageReference.name ?: BLANK_STRING,
                referenceType = referenceType
            )
        }
    }
}
