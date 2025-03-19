package com.nudge.core.database.entities.state

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.STATE_TABLE_NAME


@Entity(tableName = STATE_TABLE_NAME)
data class StateEntity(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("orderNumber")
    @Expose
    @ColumnInfo(name = "orderNumber")
    var orderNumber: Int,

    @SerializedName("language")
    @Expose
    @ColumnInfo(name = "language")
    var state: String,

    @SerializedName("localName")
    @Expose
    @ColumnInfo(name = "localName")
    val localName: String?,
)
