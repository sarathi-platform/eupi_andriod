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
            "amount",
            "are_you_sure_you_want_to_delete",
            "no",
            "yes"
        )
    ),
    DataSummaryScreen(
        "DataSummaryScreen",
        listOf(
            "From - To",
            "All Events:",
            "Last %1\$s events:",
            "Show Less",
            "Show more",
            "View edit history",
            "Event:",
            "Amount:",
            "Assets:",
            "Add Event"
        )
    ),
    DataTabScreen(
        "DataTabScreen", listOf(
            "Are you sure?", "Do you want to exit the app?" +
                    "Exit", "Cancel", "SARATHI", "Search by didis"
        )
    ),
    EditHistoryScreen(
        "EditHistoryScreen", listOf(
            "Edit history", "From - To" +
                    ""
        )
    ),
    EditHistoryRow(
        "EditHistoryRow",
        listOf("Delete", "Event:", "Asset Type:", "Increase in Number:", "Event Date:")
    ),
    NoScreen("NoScreen", emptyList())

}