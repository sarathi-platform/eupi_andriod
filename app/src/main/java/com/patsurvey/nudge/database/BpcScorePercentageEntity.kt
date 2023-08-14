package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SCORE_PERCENTAGE_TABLE

@Entity(tableName = "$BPC_SCORE_PERCENTAGE_TABLE")
data class BpcScorePercentageEntity(
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("percentage")
    @Expose
    @ColumnInfo(name = "percentage")
    val percentage: Int,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    val name: String?= BLANK_STRING,

    @SerializedName("stateId")
    @Expose
    @ColumnInfo(name = "stateId")
    val stateId: Int
)