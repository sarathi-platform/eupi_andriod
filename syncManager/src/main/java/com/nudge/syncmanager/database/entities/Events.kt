package com.nudge.syncmanager.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.syncmanager.database.converters.DateConverter
import java.util.Date

@Entity(tableName = "Events_table")
data class Events(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val created_date: Date,

    @ColumnInfo("modified_date")
    @TypeConverters(DateConverter::class)
    val modified_date: Date,

    @ColumnInfo("created_by")
    val created_by: String,

    @ColumnInfo("request_status")
    val request_status: String,

    @ColumnInfo("request_payload")
    val request_payload: String,

    @ColumnInfo("reponse_payload")
    val reponse_payload: String,

    @ColumnInfo("response_status")
    val response_status: String,

    @ColumnInfo("retry_count")
    val retry_count: Int,

    @ColumnInfo("error_message")
    val error_message: String,

    @ColumnInfo("metadata")
    val metadata: String

)



