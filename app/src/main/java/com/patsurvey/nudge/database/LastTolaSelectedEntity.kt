package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.LAST_SELECTED_TOLA_TABLE

@Entity(tableName = LAST_SELECTED_TOLA_TABLE)
data class LastTolaSelectedEntity(
    @PrimaryKey
    @ColumnInfo(name = "tolaId")
    var tolaId: Int,
    @ColumnInfo(name = "tolaName")
    var tolaName : String,

    @ColumnInfo(name = "villageId")
    var villageId : Int,
)
