package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.OPTION_TABLE
import com.nrlm.baselinesurvey.database.converters.ConditionsDtoConvertor
import com.nrlm.baselinesurvey.database.converters.ContentListConverter
import com.nrlm.baselinesurvey.database.converters.ValuesDtoConverter
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.model.response.ContentList

@Entity(tableName = OPTION_TABLE)
data class OptionItemEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    var userId: String? = BLANK_STRING,
    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    var sectionId: Int = 0,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId: Int? = 0,
    @SerializedName("optionId")
    @Expose
    val optionId: Int? = null,

    @SerializedName("description")
    @ColumnInfo(name = "display")
    val display: String? = null,

    @SerializedName("weight")
    val weight: Int? = null,

    @SerializedName("optionValue")
    val optionValue: Int? = null,

    @SerializedName("paraphrase")
    val summary: String? = null,

    @SerializedName("count")
    var count: Int? = 0,

    @SerializedName("optionImage")
    var optionImage: String? = BLANK_STRING,

    @SerializedName("type")
    var optionType: String? = BLANK_STRING,

    /*@SerializedName("questions")
    @Expose
    @TypeConverters(OptionQuestionConverter::class)
    val questionList: List<QuestionList?>? = listOf(),*/

    @SerializedName("conditional")
    @Expose
    val conditional: Boolean = false,

    @SerializedName("order")
    @Expose
    val order: Int = 0,

    @SerializedName("values")
    @Expose
    @TypeConverters(ValuesDtoConverter::class)
    val values: List<ValuesDto>? = listOf(),

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int? = 1,

    @SerializedName("optionTag")
    @Expose
    @ColumnInfo(name = "optionTag")
    val optionTag: Int = 0,

    @SerializedName("conditions")
    @Expose
    @TypeConverters(ConditionsDtoConvertor::class)
    val conditions: List<ConditionsDto?>? = emptyList(),

    @SerializedName("isSelected")
    var isSelected: Boolean? = false,

    @SerializedName("selectedValue")
    var selectedValue: String? = BLANK_STRING,

    var selectedValueId: Int = 0,

    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList> = listOf()
){
    companion object {
        fun getEmptyOptionItemEntity(): OptionItemEntity {
            return OptionItemEntity(
                id = 0,
                optionId = 0,
                sectionId = 0,
                contentEntities = listOf(),
                conditional = false,
                optionTag = 0,
                order = 0,
                surveyId = 0
            )
        }
    }
}
