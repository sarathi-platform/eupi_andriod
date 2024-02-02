package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.OPTION_TABLE
import com.nrlm.baselinesurvey.database.converters.OptionQuestionConverter
import com.nrlm.baselinesurvey.database.converters.StringConverter
import com.nrlm.baselinesurvey.model.response.QuestionList

@Entity(tableName = OPTION_TABLE)
data class OptionItemEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

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

    @SerializedName("questions")
    @Expose
    @TypeConverters(OptionQuestionConverter::class)
    val questionList: List<QuestionList?> = listOf(),
    @SerializedName("conditional")
    @Expose
    val conditional: Boolean = false,
    @SerializedName("order")
    @Expose
    val order: Int = 0,
    @SerializedName("values")
    @Expose
    @TypeConverters(StringConverter::class)
    val values: List<String> = listOf(),
    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int? = 1,
)
