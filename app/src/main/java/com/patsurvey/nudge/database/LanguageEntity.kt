package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.LANGUAGE_TABLE_NAME

@Entity(tableName = LANGUAGE_TABLE_NAME)
data class LanguageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "language")
    var language : String,

    @ColumnInfo(name = "langCode")
    val langCode : String?,
)
