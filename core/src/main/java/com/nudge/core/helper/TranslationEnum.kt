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
    NoScreen("NoScreen", emptyList())

}