package com.sarathi.dataloadingmangement.model.uiModel

import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.ConditionsDtoConvertor
import com.sarathi.dataloadingmangement.model.survey.response.ConditionsDto
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ContentListConverter

data class OptionsUiModel(
    var sectionId: Int = 0,

    val surveyId: Int,

    var questionId: Int? = 0,
    val optionId: Int? = null,


    val paraphrase: String? = null,
    val description: String? = null,

    var optionType: String? = BLANK_STRING,


    val order: Int = 0,


    val optionTag: List<Int> = listOf(),

    var isSelected: Boolean? = false,

    var selectedValue: String? = BLANK_STRING,
    var originalValue: String? = BLANK_STRING,

    var selectedValueId: Int = 0,

    @TypeConverters(ConditionsDtoConvertor::class)
    val conditions: List<ConditionsDto?>? = emptyList(),
    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList> = listOf()
)
