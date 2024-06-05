package com.sarathi.missionactivitytask.ui.grantTask.model

enum class GrantTaskCardSlots(val type: String) {
    GRANT_TASK_TITLE(type = "text"),
    GRANT_TASK_TITLE_PREFIX_ICON(type = "image"),
    GRANT_TASK_SUBTITLE_PREFIX_ICON(type = "image"),
    GRANT_TASK_SUBTITLE(type = "text"),
    GRANT_TASK_PRIMARY_BUTTON(type = "text"),
    GRANT_TASK_SECONDARY_BUTTON(type = "text"),
    GRANT_TASK_STATUS(type = "text")

}
enum class GrantTaskSearchBarSlots(val type: String) {
    GRANT_TASK_SEARCH_TITLE(type = "text"),

}