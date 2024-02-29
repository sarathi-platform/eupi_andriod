package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.CONTENT_TABLE_NAME
import com.nrlm.baselinesurvey.model.response.ContentResponse

@Entity(tableName = CONTENT_TABLE_NAME)
data class ContentEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "contentKey")
    val contentKey: String,
    val contentId: Int,
    val contentValue: String,
    val contentType: String,
//    @SerializedName("sectionId")
//    @Expose
//    @ColumnInfo(name = "sectionId")
//    val sectionId: Int? = 0,
//    val surveyId: Int? = 0,
//    val contentKey: String? = BLANK_STRING,
//    val contentType: String? = BLANK_STRING,
//    val contentValue: String? = BLANK_STRING,
//    @TypeConverters(ContentMapConverter::class)
//    var questionContentMapping: MutableList<MutableMap<Int, List<ContentList>>> = mutableListOf(),
    var languageId: Int
) {
    companion object {
        fun getContentEntity(
            contentResponse: ContentResponse
        ): ContentEntity {
            return ContentEntity(
                contentKey = contentResponse.contentKey,
                contentId = contentResponse.contentId,
                contentValue = contentResponse.contentValue,
                contentType = contentResponse.contentType,
                languageId = 2
            )
        }
    }
}