package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE

@Entity(tableName = STEPS_LIST_TABLE)
data class StepListEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "orderNumber")
    var orderNumber: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "isComplete")
    var isComplete: Boolean = false,

    @ColumnInfo(name = "needToPost")
    var needToPost: Boolean = false
)
