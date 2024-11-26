package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.ApiStatusTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.toDate
import java.util.Date

@Entity(tableName = ApiStatusTable)
data class ApiStatusEntity(

    @PrimaryKey()
    @ColumnInfo("api_end_point")
    val apiEndpoint: String,

    @ColumnInfo("status")
    val status: Int,

    @ColumnInfo("created_date")
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),

    @ColumnInfo("modified_date")
    @TypeConverters(DateConverter::class)
    val modifiedDate: Date,

    @ColumnInfo("error_message")
    val errorMessage: String,

    @ColumnInfo("error_code")
    val errorCode: Int,

    @ColumnInfo("call_screen")
    @TypeConverters(ListConvertor::class)
    val callScreen: List<String> = emptyList()
)


