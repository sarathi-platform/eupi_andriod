package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.BPC_SCORE_PERCENTAGE_TABLE

@Entity(tableName = "$BPC_SCORE_PERCENTAGE_TABLE")
data class BpcScorePercentageEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "percentage")
    val percentage: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "stateId")
    val stateId: Int
)