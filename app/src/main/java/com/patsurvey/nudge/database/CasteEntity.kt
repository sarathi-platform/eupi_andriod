package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.CASTE_TABLE

@Entity(tableName = CASTE_TABLE)
data class CasteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "casteName")
    var casteName : String
)
