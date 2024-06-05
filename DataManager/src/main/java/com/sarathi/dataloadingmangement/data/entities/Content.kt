package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.CONTENT_TABLE_NAME

@Entity(tableName = CONTENT_TABLE_NAME)
data class Content(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contentId")
    val contentId: Int,
    val contentKey: String,
    val contentValue: String,
    val contentType: String,
    var languageCode: Int,
    var thumbUrl: String,
    var isDownload: Int,
    var contentName: String
)