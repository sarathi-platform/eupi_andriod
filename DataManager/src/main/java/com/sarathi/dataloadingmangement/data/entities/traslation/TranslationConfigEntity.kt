package com.sarathi.dataloadingmangement.data.entities.traslation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.TRANSLATION_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.converters.LanguageConfigConverter
import com.sarathi.dataloadingmangement.network.response.LanguageModel

@Entity(tableName = TRANSLATION_CONFIG_TABLE_NAME)
data class TranslationConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var userId: String? = BLANK_STRING,
    var id: Int = 0,
    val key: String,
    @TypeConverters(LanguageConfigConverter::class)
    var languages: List<LanguageModel>
)
