package com.nrlm.baselinesurvey.utils.states

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.utils.DescriptionContentType

data class DescriptionContentState(
    val descriptionContentType: List<DescriptionContentType> = emptyList(),
    val textTypeDescriptionContent: String = BLANK_STRING,
    //val subTextTypeDescriptionContent: String = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not? Please check the granary/ where they store their grain and also check with neighbors also to understand the food security of the family",
    val subTextTypeDescriptionContent: String = BLANK_STRING,
    val imageTypeDescriptionContent: String = BLANK_STRING,
    val videoTypeDescriptionContent: String = BLANK_STRING
)
