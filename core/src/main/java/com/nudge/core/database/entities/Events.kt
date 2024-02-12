package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.EventsTable
import com.nudge.core.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = EventsTable)
data class Events (
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

    @ColumnInfo("request_payload")
    val request_payload: String?,

    @ColumnInfo("request_status")
    val request_status: String,

    @ColumnInfo("consumer_response_payload")
    val consumer_response_payload: String?,

    @ColumnInfo("consumer_status")
    val consumer_status: String,

    @ColumnInfo("retry_count")
    val retry_count: Int,

    @ColumnInfo("error_message")
    val error_message: String?,

    @ColumnInfo("metadata")
    val metadata: String?

)