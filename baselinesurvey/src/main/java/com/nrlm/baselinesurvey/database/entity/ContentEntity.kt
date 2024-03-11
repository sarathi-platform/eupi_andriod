package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.CONTENT_TABLE_NAME

@Entity(tableName = CONTENT_TABLE_NAME)
data class ContentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contentId")
    val contentId: Int,
    val contentKey: String,
    val contentValue: String,
    val contentType: String,
    var languageCode: Int
)