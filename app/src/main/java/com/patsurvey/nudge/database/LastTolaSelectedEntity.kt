package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.LAST_SELECTED_TOLA_TABLE

@Entity(tableName = LAST_SELECTED_TOLA_TABLE)
data class LastTolaSelectedEntity(
    @PrimaryKey
    @SerializedName("tolaId")
    @Expose
    @ColumnInfo(name = "tolaId")
    var tolaId: Int,

    @SerializedName("tolaName")
    @Expose
    @ColumnInfo(name = "tolaName")
    var tolaName : String,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId : Int,
)
