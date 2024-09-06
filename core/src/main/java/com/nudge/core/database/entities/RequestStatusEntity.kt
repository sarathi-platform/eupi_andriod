package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.RequestStatusTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date

@Entity(tableName = RequestStatusTable)
data class RequestStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var eventStatusId: Int,

    @ColumnInfo("status")
    val status: String? = BLANK_STRING,

    @ColumnInfo("requestId")
    val requestId: String? = BLANK_STRING,

    @ColumnInfo("createdDate")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modifiedDate")
    @TypeConverters(DateConverter::class)
    val modifiedDate: Date,

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobileNumber")
    val mobileNumber: String,

    @ColumnInfo("eventCount")
    val eventCount: Int? = 0,

    )
