package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.patsurvey.nudge.utils.DIDI_TABLE


@Entity(tableName = DIDI_TABLE)
data class DidiEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "address")
    var address : String,

    @ColumnInfo(name = "guardianName")
    var guardianName : String,

    @ColumnInfo(name = "relationship")
    var relationship : String,

    @ColumnInfo(name = "castId")
    var castId : Int,

    @ColumnInfo(name = "castName")
    var castName : String,

    @ColumnInfo(name = "cohortId")
    var cohortId : Int,

    @ColumnInfo(name = "cohortName")
    var cohortName : String
)
