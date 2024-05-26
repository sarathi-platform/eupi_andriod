package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.ACTIVITY_CONFIG_LANGUAGE_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING


@Entity(tableName = ACTIVITY_CONFIG_LANGUAGE_ATTRIBUTE_TABLE_NAME)
data class ActivityConfigLanguageAttributesEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var languageCode: String,
    var key: String,
    var type: String,
    var value: String,
    var componentType: String,
    var language: String,
    var activityId: Int,
    var missionId: Int
)
