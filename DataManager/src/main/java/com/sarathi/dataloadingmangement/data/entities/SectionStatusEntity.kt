package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.nudge.core.BLANK_STRING

data class SectionStatusEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "userId")
    var userId: String? = BLANK_STRING,

    @ColumnInfo(name = "missionId")
    val missionId: Int,

    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @ColumnInfo(name = "sectionId")
    val sectionId: Int,

    @ColumnInfo(name = "taskId")
    val taskId: Int,

    @ColumnInfo(name = "sectionStatus")
    val sectionStatus: String?,
)
