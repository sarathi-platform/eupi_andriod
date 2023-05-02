package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "village_list_table")
data class VillageList(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "is_completed")
    var is_completed: Boolean = false,
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = false
)
