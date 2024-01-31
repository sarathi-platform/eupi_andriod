package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventsTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.MetadataDtoConverter
import com.nudge.core.database.converters.StringJsonConverter
import com.nudge.core.model.MetadataDto
import com.nudge.core.toDate
import java.util.Date
import java.util.UUID

@Entity(tableName = EventsTable)
data class Events (
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(), // TODO add mobile number and timestamp

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("type")
    val type: String,

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

    //TODO change name to status and use it as a common status for both producer and consumer status
    @ColumnInfo("request_status")
    val request_status: String,

    @ColumnInfo("consumer_response_payload") //TODO change name to result.
    val consumer_response_payload: String?,

    @ColumnInfo("consumer_status") //TODO Remove this
    val consumer_status: String,

    // TODO Move this to other table
    @ColumnInfo("retry_count") // TODO
    val retry_count: Int = 0,

    // TODO Move this to other table
    @ColumnInfo("error_message")
    val error_message: String? = BLANK_STRING,

    @ColumnInfo("metadata")
    val metadata: String?

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
                request_status = BLANK_STRING,
                consumer_response_payload = BLANK_STRING,
                consumer_status = BLANK_STRING,
                metadata = null,
                mobile_number = BLANK_STRING
            )
        }
    }
}

fun <T> String.getPayloadFromString(): T? {
    val type = object : TypeToken<T?>() {}.type
    return Gson().fromJson(this, type)
}


