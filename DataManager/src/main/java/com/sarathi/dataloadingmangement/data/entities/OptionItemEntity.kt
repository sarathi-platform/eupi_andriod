package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.OPTION_TABLE
import com.sarathi.dataloadingmangement.data.converters.ConditionsDtoConvertor
import com.sarathi.dataloadingmangement.data.converters.IntConverter
import com.sarathi.dataloadingmangement.data.converters.ValuesDtoConverter
import com.sarathi.dataloadingmangement.model.survey.response.ConditionsDto
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ContentListConverter
import com.sarathi.dataloadingmangement.model.survey.response.OptionsItem
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto

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

    @SerializedName("optionTag")
    @Expose
    @ColumnInfo(name = "optionTag")
    @TypeConverters(IntConverter::class)
    val optionTag: List<Int> = listOf(),

    @SerializedName("conditions")
    @Expose
    @TypeConverters(ConditionsDtoConvertor::class)
    val conditions: List<ConditionsDto?>? = emptyList(),

    @SerializedName("isSelected")
    var isSelected: Boolean? = false,

    @SerializedName("selectedValue")
    var selectedValue: String? = BLANK_STRING,

    @SerializedName("originalValue")
    var originalValue: String? = BLANK_STRING,

    var selectedValueId: Int = 0,

    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList> = listOf()
) {
    companion object {
        fun getEmptyOptionItemEntity(): OptionItemEntity {
            return OptionItemEntity(
                id = 0,
                optionId = 0,
                sectionId = 0,
                contentEntities = listOf(),
                conditional = false,
                optionTag = listOf(),
                order = 0,
                surveyId = 0
            )
        }


        fun getOptionItemEntity(
            userId: String,
            optionsItem: OptionsItem,
            sectionId: Int,
            surveyId: Int,
            questionId: Int?,
        ): OptionItemEntity {
            return OptionItemEntity(
                id = 0,
                userId = userId,
                optionId = optionsItem.optionId,
                questionId = questionId,
                sectionId = sectionId,
                surveyId = surveyId,
                weight = optionsItem.weight,
                optionValue = optionsItem.optionValue,
                count = optionsItem.count,
                optionImage = optionsItem.optionImage,
                optionType = optionsItem.optionType,
                conditional = optionsItem.conditional,
                order = optionsItem.order,
                values = optionsItem.values,
                conditions = optionsItem.conditions,
                optionTag = optionsItem.tag ?: listOf(),
                originalValue = optionsItem.originalValue,
                contentEntities = optionsItem.contentList ?: listOf()
            )
        }
    }
}


