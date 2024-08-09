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

}
enum class GrantTaskSearchBarSlots(val type: String) {
    GRANT_TASK_SEARCH_TITLE(type = "text"),

}