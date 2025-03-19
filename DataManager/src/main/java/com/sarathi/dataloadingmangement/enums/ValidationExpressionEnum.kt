package com.sarathi.dataloadingmangement.enums

enum class ValidationExpressionEnum(val originalValue: String) {

    TOTAL_ASSET_COUNT(originalValue = "#TOTAL_ASSET_COUNT#"),
    ASSET_TYPE(originalValue = "#ASSET_TYPE#"),
    SELECTED_ASSET_COUNT(originalValue = "#SELECTED_ASSET_COUNT#"),
    TOTAL_SELECTED_ASSET_COUNT(originalValue = "#TOTAL_SELECTED_ASSET_COUNT#"),
    SELECTED_AMOUNT(originalValue = "#SELECTED_AMOUNT#"),
    FROM_SELECTED_ASSET_TYPE(originalValue = "#FROM_SELECTED_ASSET_TYPE#"),
    TO_SELECTED_ASSET_TYPE(originalValue = "#TO_SELECTED_ASSET_TYPE#"),

    INPUT_VALUE(originalValue = "#INPUT_VALUE#"),
    INPUT_LENGTH(originalValue = "#INPUT_LENGTH#")

}