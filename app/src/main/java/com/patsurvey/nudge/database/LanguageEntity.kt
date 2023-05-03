package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.LANGUAGE_TABLE_NAME

@Entity(tableName = LANGUAGE_TABLE_NAME)
data class LanguageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "code")
    val code : String?,

    @ColumnInfo(name = "isSelected")
    var isSelected : Boolean
)
