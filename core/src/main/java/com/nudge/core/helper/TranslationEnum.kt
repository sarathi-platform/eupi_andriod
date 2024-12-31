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
    DidiTabScreen("DidiTabScreen", listOf("are_you_sure" ,
            "do_you_want_to_exit_the_app" ,"exit","cancel",
        "refresh_failed_please_try_again","app_name","not_able_to_load",
        "no_didi_s_assigned_to_you","search_didi","search_by_small_groups",
        "total_didis")),

    SmallGroupAttendanceScreen("SmallGroupAttendanceScreen", listOf("confirmation_alert_dialog_title","do_you_want_mark_all_absent","yes","no","search_didi","all")),

    SmallGroupAttendanceEditScreen("SmallGroupAttendanceEditScreen", listOf("confirmation_alert_dialog_title","delete_attendance_confirmation_msg",
        "yes","no","submit","data_change_not_allow","ok","all")),

    SmallGroupAttendanceHistoryScreen("SmallGroupAttendanceHistoryScreen", listOf("confirmation_alert_dialog_title",
        "do_you_want_mark_all_absent","yes","no","take_attendance_button_text",
        "date_range_picker_label_text","date_range_picker_label_text",
        "attendance_history_header_text","total_didis_label_text","take_attendance_button_text"
    ,"attendance_percentage_text","total_count_text","edit_button_text"
    ,"delete_button_text","absent")),
    NoScreen("NoScreen", emptyList())

}