package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.DIDI_SECTION_PROGRESS_TABLE
import com.nudge.core.BLANK_STRING

@Entity(tableName = DIDI_SECTION_PROGRESS_TABLE)
data class DidiSectionProgressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    var userId: String? = BLANK_STRING,

    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @ColumnInfo(name = "sectionId")
    val sectionId: Int,

    @ColumnInfo(name = "didiId")
    val didiId: Int,

    @ColumnInfo(name = "sectionStatus")
    val sectionStatus: Int = -1,

    )
