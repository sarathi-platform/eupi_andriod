package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.TOLA_TABLE

@Entity(tableName = TOLA_TABLE)
data class TolaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "latitude")
    var latitude: Int,
    @ColumnInfo(name = "longitude")
    var longitude: Int,
    @ColumnInfo(name = "villageId")
    var villageId: Int,
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true
)
