package com.nudge.core.helper


enum class TranslationEnum(val screenName: String, val keys: List<String> = emptyList()) {
    CommonStrings(
        "CommonStrings",
        listOf(
            "didi_sub_tab_title",
            "small_group_sub_tab_title",
            "all",
            "no_entry_this_month",
            "no_entry_this_week",
            "last_week",
            "last_month",
            "last_3_months",
            "custom_date",
            "reopen_activity_step_1",
            "reopen_activity_step_2",
            "ongoing_tab_title",
            "completed_tab_title",
            "mission",
            "data",
            "didis_item_text_plural",
            "progress_item_text",
            "didis_item_text_plural",
            "general_missions_filter_label",
            "all_missions_filter_label",
            "update_available",
            "version_available_message",
            "app_update_cancel",
            "app_update",
            "invalid_url",
            "str_app_update_fail",
            "str_download_complete",
            "click_here_to_install"
        )
    ),
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
            "yes",
            "type_of_adult_asset",
            "type_of_child_asset",
            "enter_child_asset_count"
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
            "livelihood",
            "exclude_in_calculation_message"
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
    DidiTabScreen(
        "DidiTabScreen", listOf(
            "are_you_sure",
            "do_you_want_to_exit_the_app", "exit", "cancel",
            "refresh_failed_please_try_again", "app_name", "not_able_to_load",
            "no_didi_s_assigned_to_you", "search_didi", "search_by_small_groups",
            "total_didis"
        )
    ),

    SmallGroupAttendanceScreen(
        "SmallGroupAttendanceScreen",
        listOf(
            "confirmation_alert_dialog_title",
            "do_you_want_mark_all_absent",
            "yes",
            "no",
            "search_didi",
            "all",
            "attendance_already_marked", "attendance_submitted_msg"
        )
    ),

    SmallGroupAttendanceEditScreen(
        "SmallGroupAttendanceEditScreen", listOf(
            "confirmation_alert_dialog_title",
            "do_you_want_mark_all_absent",
            "attendance_submitted_msg",
            "yes", "no", "submit", "data_change_not_allow", "ok", "all"
        )
    ),

    SmallGroupAttendanceHistoryScreen(
        "SmallGroupAttendanceHistoryScreen",
        listOf(
            "confirmation_alert_dialog_title",
            "delete_attendance_confirmation_msg",
            "yes",
            "no",
            "take_attendance_button_text",
            "date_range_picker_label_text",
            "attendance_history_header_text",
            "total_didis_label_text",
            "attendance_percentage_text",
            "total_count_text",
            "edit_button_text",
            "delete_button_text",
            "present",
            "absent"
        )
    ),
    NoScreen("NoScreen", emptyList()),

    SubmitPhysicalFormScreen("SubmitPhysicalFormScreen", listOf("go_back")),
    BasicMissionCard("BasicMissionCard", listOf("livelihood")),

    ActivityScreen(
        "ActivityScreen",
        listOf(
            "complete_mission",
            "file_not_exists",
            "not_be_able_to_make_changes_after_completing_this_mission",
            "cancel",
            "ok",
            "refresh_failed_please_try_again"
        )
    ),
    MissionScreen(
        "MissionScreen",
        listOf(
            "refresh_failed_please_try_again",
            "are_you_sure",
            "do_you_want_to_exit_the_app",
            "exit",
            "cancel",
            "data_not_Loaded",
            "ok",
            "search",
            "start",
            "missions_filter_label_suffix"
        )
    ),
    DisbursementFormSummaryScreen(
        "DisbursementFormSummaryScreen",
        listOf(
            "disbursement_summary",
            "share",
            "download",
            "amount",
            "close",
            "csg_disbursed",
            "didis"
        )
    ),
    TaskScreen(
        "TaskScreen",
        listOf(
            "not_available",
            "in_progress",
            "task_view",
            "continue_text",
            "since_you_have_completed_all_the_tasks_please_complete_the_activity",
            "on_completing_the_activity_you_will_not_be_able_to_edit_the_details",
            "complete_activity",
            "activity_completion_message",
            "complete_activity",
            "empty_task_list_placeholder",
            "no_result_found",
            "not_be_able_to_make_changes_after_completing_this_activity",
            "cancel",
            "ok",
            "filter_item_count_label",
            "activity_completed_unable_to_edit",
            "refresh_failed_please_try_again",
            "no_small_group_assgned_label",
            "small_group_filter_label",
            "not_available",
            "activity_completed_unable_to_edit",
            "contact_to_admin_id_missing"
        )
    ),
    LivelihoodDropDownScreen(
        "LivelihoodDropDownScreen",
        listOf(
            "are_you_sure",
            "form_alert_dialog_message",
            "proceed_txt",
            "cancel_txt",
            "primary_and_secondary_value_not_same",
            "submit",
            "select_first_livelihood_for_didi",
            "select_second_livelihood_for_didi"
        )
    )

}