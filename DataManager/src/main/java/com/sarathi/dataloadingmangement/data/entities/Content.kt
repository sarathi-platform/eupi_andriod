package com.sarathi.dataloadingmangement.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.CONTENT_TABLE_NAME

@Entity(tableName = CONTENT_TABLE_NAME)
data class Content(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val contentId: String,
    val contentKey: String,
    val contentValue: String,
    val contentType: String,
    var languageCode: String,
    var thumbUrl: String,
    var isDownload: Int,
    var contentName: String
)