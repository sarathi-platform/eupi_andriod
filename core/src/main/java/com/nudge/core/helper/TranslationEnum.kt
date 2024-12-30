package com.nudge.core.helper


enum class TranslationEnum(val screenName: String, val keys: List<String> = emptyList()) {
    AddEventScreen(
        "AddEventScreen",
        listOf(
            "edit_event",
            "add_event",
            "delete",
            "save_text",
            "date",
            "select",
            "livelihood",
            "events",
            "type_of_asset",
            "products",
            "increase_in_number",
            "decrease_in_number",
            "amount",
            "are_you_sure_you_want_to_delete",
            "event_added_successfully",
            "no",
            "yes"
        )
    ),
    DataSummaryScreen(
        "DataSummaryScreen",
        listOf(
            "date_range_picker_label_text",
            "all_events",
            "last_events",
            "show_less",
            "show_more",
            "view_edit_history",
            "event",
            "amount",
            "asset",
            "add_event",
            "livelihood"
        )
    ),
    DataTabScreen(
        "DataTabScreen", listOf(
            "are_you_sure", "do_you_want_to_exit_the_app" +
                    "exit", "cancel", "app_name", "search_by_didis"
        )
    ),
    EditHistoryScreen(
        "EditHistoryScreen", listOf(
            "edit_history", "date_range_picker_label_text"
        )
    ),
    EditHistoryRow(
        "EditHistoryRow",
        listOf("delete", "event", "asset_type", "increse_in_number", "event_date")
    ),
    NoScreen("NoScreen", emptyList())

}