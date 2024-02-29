package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.SECTION_TABLE

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    val sectionId: Int = 0,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @SerializedName("sectionName")
    @Expose
    @ColumnInfo(name = "sectionName")
    val sectionName: String = NO_SECTION,

    @SerializedName("sectionOrder")
    @Expose
    @ColumnInfo(name = "sectionOrder")
    val sectionOrder: Int = 1,

    @SerializedName("sectionDetails")
    @Expose
    @ColumnInfo(name = "sectionDetails")
    val sectionDetails: String = BLANK_STRING,

    @SerializedName("sectionIcon")
    @Expose
    @ColumnInfo(name = "sectionIcon")
    val sectionIcon: String = BLANK_STRING,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    val languageId: Int,
    val questionSize: Int = 0
)
