package com.nudge.syncmanager.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.syncmanager.database.converters.DateConverter
import com.nudge.syncmanager.database.converters.StringJsonConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "Events_table")
data class Events(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

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
    @TypeConverters(StringJsonConverter::class)
    val request_payload: String,

    @ColumnInfo("reponse_payload")
    @TypeConverters(StringJsonConverter::class)
    val reponse_payload: String,

    @ColumnInfo("response_status")
    val response_status: String,

    @ColumnInfo("retry_count")
    val retry_count: Int,

    @ColumnInfo("error_message")
    val error_message: String?,

    @ColumnInfo("metadata")
    @TypeConverters(StringJsonConverter::class)
    val metadata: String

)



