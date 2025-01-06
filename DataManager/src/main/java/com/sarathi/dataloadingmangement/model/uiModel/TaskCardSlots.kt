package com.sarathi.dataloadingmangement.model.uiModel

enum class TaskCardSlots(val type: String) {
    TASK_TITLE(type = "text"),
    TASK_TITLE_PREFIX_ICON(type = "image"),
    TASK_SUBTITLE_PREFIX_ICON(type = "image"),
    TASK_SUBTITLE(type = "text"),
    TASK_SUBTITLE_2(type = "text"),
    TASK_SUBTITLE_3(type = "text"),
    TASK_SUBTITLE_4(type = "text"),
    TASK_SUBTITLE_5(type = "text"),
    TASK_SUBTITLE_6(type = "text"),
    TASK_SUBTITLE_8(type = "text"),
    TASK_PRIMARY_BUTTON(type = "text"),
    TASK_SECONDARY_BUTTON(type = "text"),
    TASK_IMAGE(type = "image"),
    SEARCH_ON(type = "text"),
    GROUP_BY(type = "text"),
    TASK_STATUS(type = "text"),
    TASK_SECOND_STATUS_AVAILABLE(type = "text"),
    TASK_NOT_AVAILABLE_ENABLE(type = "text"),
    SEARCH_LABEL(type = "text"),

    TASK_PROGRESS(type = "text"),
    FILTER_BY(type = "text")
}

enum class SurveyConfigCardSlots {
    FORM_QUESTION_CARD_TITLE,
    FORM_QUESTION_CARD_BUTTON,
    FORM_QUESTION_CARD_TOTAL_COUNT,
    FORM_SUMMARY_CARD_EDIT_BUTTON,
    FORM_SUMMARY_CARD_DELETE_BUTTON,
    FORM_SUMMARY_CARD_SUBJECT_NAME,
    FORM_SUMMARY_CARD_SUBJECT_RELATIONSHIP,
    FORM_SUMMARY_CARD_SUBJECT_AGE,
    FORM_SUMMARY_CARD_SUBJECT_HUSBAND_NAME,
    FORM_SUMMARY_CARD_AADHAR,
    FORM_SUMMARY_CARD_VOTER,
    FORM_SUMMARY_CARD_PHONE_NUMBER,
    FORM_SUMMARY_CARD_IMAGE,
    FORM_MAX_RESPONSE_COUNT,
    FORM_PREPOPULATED_FIELD_SUBJECT_NAME,
    FORM_FORM_PREPOPULATED_FIELD_SUBJECT_HUSBAND_NAME,
    FORM_FORM_PREPOPULATED_FIELD_SUBJECT_CASTE_NAME,
    FORM_QUESTION_CARD_SUBTITLE_LABLE,
    FORM_QUESTION_CARD_SUBTITLE_VALUE,
    CONFIG_AUTO_CALCULATE;

    companion object {
        const val CONFIG_SLOT_TYPE_TAG = "tag"
        const val CONFIG_SLOT_TYPE_PREPOPULATED = "FormPrePopulatedField"
        const val CONFIG_SLOT_TYPE_QUESTION_CARD = "QuestionCard"

        const val CALCULATION_TYPE = "calculation"
        const val CASTE_ID = "casteId"
    }
}

enum class GrantTaskSearchBarSlots(val type: String) {
    GRANT_TASK_SEARCH_TITLE(type = "text"),

}