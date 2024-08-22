package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.ImageStatusTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date
import java.util.UUID

@Entity(tableName = ImageStatusTable)
data class ImageStatusEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modified_date")
    @TypeConverters(DateConverter::class)
    val modifiedDate: Date,

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobile_number")
    val mobileNumber: String,

    @ColumnInfo("file_name")
    val fileName: String,


    @ColumnInfo("file_path")
    val filePath: String,

    @ColumnInfo("status")
    val status: String,

    @ColumnInfo("retry_count")
    val retryCount: Int = 0,

    @ColumnInfo("error_message")
    val errorMessage: String? = BLANK_STRING,

    @ColumnInfo("image_event_id")
    val imageEventId: String? = BLANK_STRING,

    @ColumnInfo("request_id")
    val requestId: String? = BLANK_STRING,

)
