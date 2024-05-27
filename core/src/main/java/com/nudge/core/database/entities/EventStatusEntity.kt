package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventsStatusTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date

@Entity(tableName = EventsStatusTable)
data class EventStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var eventStatusId: Int,

    @ColumnInfo(name = "client_id")
    val clientId: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobile_number")
    val mobileNumber: String,

    @ColumnInfo("status")
    val status: String,

    @ColumnInfo("error_message")
    val errorMessage: String? = BLANK_STRING,
)
