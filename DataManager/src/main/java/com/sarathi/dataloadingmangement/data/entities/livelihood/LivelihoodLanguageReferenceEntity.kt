package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.LIVILIHOOD_LANGUAGE_TABLE_NAME

@Entity(tableName = LIVILIHOOD_LANGUAGE_TABLE_NAME)
data class LivelihoodLanguageReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("primaryKey")
    @Expose
    @ColumnInfo(name = "primaryKey")
    var primaryKey: Int = 0,
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "languageCode")
    var languageCode: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "userId")
    var userId: String,
) {
    companion object {
        fun getLivelihoodLanguageEntity(
            id: Int,
            languageCode: String,
            name: String,
            uniqueUserIdentifier: String,
        ): LivelihoodLanguageReferenceEntity {
            return LivelihoodLanguageReferenceEntity(
                primaryKey = 0,
                id = id,
                userId = uniqueUserIdentifier,
                languageCode = languageCode,
                name = name,
            )
        }
    }
}
