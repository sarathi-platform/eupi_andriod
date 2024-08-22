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

    @ColumnInfo("request_id")
    val requestId: String? = BLANK_STRING,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modified_date")
    @TypeConverters(DateConverter::class)
    val modified_date: Date,

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobile_number")
    val mobileNumber: String,

    @ColumnInfo("event_count")
    val eventCount: Int? = 0,


    )
