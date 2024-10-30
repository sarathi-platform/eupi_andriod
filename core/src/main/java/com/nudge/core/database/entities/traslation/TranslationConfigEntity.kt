package com.nudge.core.database.entities.traslation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.TRANSLATION_CONFIG_TABLE_NAME
import com.nudge.core.model.response.LanguageModel

@Entity(tableName = TRANSLATION_CONFIG_TABLE_NAME)
data class TranslationConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "userId")
    var userId: String = BLANK_STRING, // Assuming BLANK_STRING is a constant defined elsewhere

    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "value")
    val value: String,

    @ColumnInfo(name = "languageCode")
    val languageCode: String
) {
    companion object {
        /**
         * Factory method to create TranslationConfigEntity.
         * @param userId User ID.
         * @param key Translation key.
         * @param languages LanguageModel containing the language code and value.
         * @return A new TranslationConfigEntity.
         */
        fun getTranslationConfigEntity(
            userId: String,
            key: String?,
            languages: LanguageModel?,
        ): TranslationConfigEntity {
            return TranslationConfigEntity(
                id = 0,
                userId = userId,
                key = key ?: BLANK_STRING,
                languageCode = languages?.languageCode ?: BLANK_STRING,
                value = languages?.value ?: BLANK_STRING
            )
        }
    }
}
