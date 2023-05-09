package com.patsurvey.nudge.database

import androidx.room.*
import com.patsurvey.nudge.utils.VILLAGE_TABLE_NAME

@Entity(tableName = VILLAGE_TABLE_NAME)
data class VillageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name : String,

    @TypeConverters(IntConverter::class)
    @ColumnInfo(name = "steps_completed")
    var steps_completed: List<Int>?,

    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = false
)
