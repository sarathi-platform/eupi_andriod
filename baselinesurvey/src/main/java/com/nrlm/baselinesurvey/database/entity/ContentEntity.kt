package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.CONTENT_TABLE_NAME
import com.nrlm.baselinesurvey.database.converters.ContentMapConverter
import com.nrlm.baselinesurvey.model.response.ContentList

@Entity(tableName = CONTENT_TABLE_NAME)
data class ContentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    val sectionId: Int? = 0,
    val surveyId: Int? = 0,
    val contentKey: String? = BLANK_STRING,
    val contentType: String? = BLANK_STRING,
    val contentValue: String? = BLANK_STRING,
    @TypeConverters(ContentMapConverter::class)
    var questionContentMapping: MutableList<MutableMap<Int, List<ContentList>>> = mutableListOf(),
    var languageId: Int
)