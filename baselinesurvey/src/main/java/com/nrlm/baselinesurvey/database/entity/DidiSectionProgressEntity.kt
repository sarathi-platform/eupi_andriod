package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.DIDI_SECTION_PROGRESS_TABLE

@Entity(tableName = DIDI_SECTION_PROGRESS_TABLE)
data class DidiSectionProgressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    val userId: Int? = -1,

    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @ColumnInfo(name = "sectionId")
    val sectionId: Int,

    @ColumnInfo(name = "didiId")
    val didiId: Int,

    @ColumnInfo(name = "sectionStatus")
    val sectionStatus: Int = -1,

    )
