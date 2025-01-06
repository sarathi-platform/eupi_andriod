package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventsTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import java.util.Date
import java.util.UUID

@Entity(tableName = EventsTable)
data class Events (
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    var id: String = UUID.randomUUID().toString(), // TODO add mobile number and timestamp

    @ColumnInfo("name")
    var name: String,

    @ColumnInfo("type")
    var type: String,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val created_date: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modified_date")
    @TypeConverters(DateConverter::class)
    val modified_date: Date,

    @ColumnInfo("createdBy")
    val createdBy: String,

    @ColumnInfo("mobile_number")
    val mobile_number: String,

    @ColumnInfo("request_payload")
    val request_payload: String?,

    @ColumnInfo("status")
    val status: String,

    @ColumnInfo("result")
    val result: String? = BLANK_STRING,

    @ColumnInfo("consumer_status")
    val consumer_status: String = BLANK_STRING,

    @ColumnInfo("retry_count")
    var retry_count: Int = 0,

    @ColumnInfo("error_message")
    val error_message: String? = BLANK_STRING,

    @ColumnInfo("metadata")
    var metadata: String?,

    @ColumnInfo("payloadLocalId")
    val payloadLocalId: String?,

    @ColumnInfo("requestId")
    val requestId:String?= BLANK_STRING,

    @ColumnInfo("eventId")
    val eventId: String? = BLANK_STRING
) {
    companion object {
        fun getEmptyEvent(): Events {
            return Events(
                id = BLANK_STRING,
                name = BLANK_STRING,
                type = BLANK_STRING,
                createdBy = BLANK_STRING,
                modified_date = System.currentTimeMillis().toDate(),
                request_payload = BLANK_STRING,
                status = BLANK_STRING,
                metadata = null,
                mobile_number = BLANK_STRING,
                payloadLocalId = BLANK_STRING
            )
        }
    }
}



