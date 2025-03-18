package com.sarathi.dataloadingmangement.model.uiModel

import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.ConditionsDtoConvertor
import com.sarathi.dataloadingmangement.model.survey.response.ConditionsDto
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ContentListConverter

data class OptionsUiModel(
    @SerializedName("sectionId", alternate = arrayOf("a")) var sectionId: Int = 0,
    @SerializedName("surveyId", alternate = arrayOf("b")) val surveyId: Int,
    @SerializedName("questionId", alternate = arrayOf("c"))

    var questionId: Int? = 0,
    @SerializedName("optionId", alternate = arrayOf("d"))

    val optionId: Int? = null,

    @SerializedName("paraphrase", alternate = arrayOf("e"))

    val paraphrase: String? = null,
    @SerializedName("description", alternate = arrayOf("f"))

    var description: String? = null,

    @SerializedName("optionType", alternate = arrayOf("g"))

    var optionType: String? = BLANK_STRING,

    @SerializedName("order", alternate = arrayOf("h")) val order: Int = 0,
    @SerializedName("isSelected", alternate = arrayOf("i")) var isSelected: Boolean? = false,
    @SerializedName("selectedValue", alternate = arrayOf("j"))

    var selectedValue: String? = BLANK_STRING,
    @SerializedName("originalValue", alternate = arrayOf("k"))

    var originalValue: String? = BLANK_STRING,
    @SerializedName("selectedValueId", alternate = arrayOf("l"))

    var selectedValueId: Int = 0,

    @SerializedName(
        "optionImage",
        alternate = arrayOf("m")
    ) var optionImage: String? = BLANK_STRING,
    @SerializedName(
        "conditions", alternate = arrayOf("n")
    ) @TypeConverters(ConditionsDtoConvertor::class) val conditions: List<ConditionsDto?>? = emptyList(),

    @SerializedName("contentEntities", alternate = arrayOf("o")) @TypeConverters(
        ContentListConverter::class
    ) val contentEntities: List<ContentList> = listOf()
)
