package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.IMAGE_STATUS_TABLE_NAME
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date
import java.util.UUID

@Entity(tableName = IMAGE_STATUS_TABLE_NAME)
data class ImageStatusEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("createDate")
    @TypeConverters(DateConverter::class)
    val createdDate: Date? = System.currentTimeMillis().toDate(),

    @ColumnInfo("modifiedDate")
    @TypeConverters(DateConverter::class)
    val modifiedDate: Date,

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobileNumber")
    val mobileNumber: String,

    @ColumnInfo("fileName")
    val fileName: String? = BLANK_STRING,

    @ColumnInfo("filePath")
    val filePath: String? = BLANK_STRING,

    @ColumnInfo("status")
    val status: String,

    @ColumnInfo("retryCount")
    val retryCount: Int? = 0,

    @ColumnInfo("errorMessage")
    val errorMessage: String? = BLANK_STRING,

    @ColumnInfo("imageEventId")
    val imageEventId: String? = BLANK_STRING,

    @ColumnInfo("requestId")
    val requestId: String? = BLANK_STRING,

    @ColumnInfo("blobUrl")
    val blobUrl: String? = BLANK_STRING,


    )
