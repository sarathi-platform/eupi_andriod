package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.NO_SECTION
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SECTION_TABLE
import com.sarathi.dataloadingmangement.data.converters.ContentListConverter
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.Sections

@Entity(tableName = SECTION_TABLE)
data class SectionEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    var userId: String? = BLANK_STRING,
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
    val questionSize: Int = 0,
    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList>?
) {
    companion object {
        fun getSectionEntity(
            userId: String,
            section: Sections,
            surveyId: Int
        ): SectionEntity {
            return SectionEntity(
                id = 0,
                userId = userId,
                sectionId = section.sectionId,
                surveyId = surveyId,
                sectionName = section.originalValue,
                sectionOrder = section.sectionOrder,
                sectionIcon = section.sectionIcon ?: BLANK_STRING,
                questionSize = section.questionList?.size ?: 0,
                contentEntities = section.contentList ?: listOf()
            )

        }
    }
}
