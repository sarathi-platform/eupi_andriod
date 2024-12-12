package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.EVENT_STATUS_TABLE_NAME
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date

@Entity(tableName = EVENT_STATUS_TABLE_NAME)
data class EventStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var eventStatusId: Int,

    @ColumnInfo(name = "clientId")
    val clientId: String,

    @ColumnInfo("createdDate")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobileNumber")
    val mobileNumber: String,

    @ColumnInfo("status")
    val status: String,

    @ColumnInfo("requestId")
    val requestId: String? = BLANK_STRING,

    @ColumnInfo("errorMessage")
    val errorMessage: String? = BLANK_STRING,
)
