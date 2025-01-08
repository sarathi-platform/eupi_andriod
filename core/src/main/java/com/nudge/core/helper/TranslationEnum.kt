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
            "yes",
            "event_deleted_successfully"
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
            "are_you_sure",
            "do_you_want_to_exit_the_app",
            "exit",
            "cancel",
            "app_name",
            "search_by_didis",
            "refresh_failed_please_try_again",
            "results_for",
            "showing"
        )
    ),
    EditHistoryScreen(
        "EditHistoryScreen", listOf(
            "edit_history",
            "date_range_picker_label_text"
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
            "attendance_already_marked",
            "submit"
        )
    ),

    SmallGroupAttendanceEditScreen(
        "SmallGroupAttendanceEditScreen", listOf(
            "confirmation_alert_dialog_title",
            "delete_attendance_confirmation_msg",
            "yes",
            "no",
            "submit",
            "data_change_not_allow",
            "ok",
            "all",
            "data_selector"
        )
    ),

    SmallGroupAttendanceHistoryScreen(
        "SmallGroupAttendanceHistoryScreen",
        listOf(
            "confirmation_alert_dialog_title",
            "do_you_want_mark_all_absent",
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

    SettingBSScreen(
        "SettingBSScreen",
        listOf(
            "settings_screen_title",
            "language_text",
            "forms",
            "training_videos",
            "export_backup_file",
            "export_data",
            "backup_recovery",
            "profile",
            "logout",
            "sync_running",
            "ok",
            "logout_confirmation",
            "cancel",
            "data_is_not_available_for_sync_please_perform_some_action",
            "share_export_file",
            "something_went_wrong"
        )
    ),
    LanguageScreen(
        "LanguageScreen",
        listOf(
            "language_text",
            "choose_language",
            "this_language_is_not_available_for_selection",
            "continue_text"
        )
    ),

    SettingFormsScreen(
        "SettingFormsScreen",
        listOf(
            "forms",
            "no_form_available_yet_text",
            "digital_form_a_title",
            "digital_form_b_title",
            "digital_form_c_title",
            "no_data_form_e_not_generated_text",
            "no_data_form_a_not_generated_text",
            "no_data_form_b_not_generated_text",
            "no_data_form_c_not_generated_text",
        )
    ),

    ExportImportScreen(
        "ExportImportScreen",
        listOf(
            "backup_recovery",
            "import_data",
            "load_server_data",
            "regenerate_all_events",
            "mark_activity_inprogress_label",
            "refresh_config",
            "are_you_sure",
            "import_restart_dialog_message",
            "proceed",
            "cancel_text",
            "share_export_file",
            "network_not_available_message",
            "are_you_sure_you_want_to_load_data_from_server",
            "yes_text",
            "option_no",
            "import_restart_dialog_message",
            "are_you_sure_you_want_to_reset_app_configuration",
            "continue_text",
            "are_you_sure_you_want_to_regenerate_events_there_will_be_data_loss_after_this_action",
            "are_you_sure_you_want_proceed_with_action"
        )
    ),
    ExportBackupScreen(
        "ExportBackupScreen",
        listOf(
            "export_data",
            "export_images",
            "export_event_file",
            "export_database",
            "export_log_file",
            "export_baseline_qna",
            "no_data_available_at_the_moment",
            "no_logs_available"
        )
    ),

    ProfileBSScreen(
        "ProfileBSScreen",
        listOf(
            "profile",
            "profile_name",
            "profile_phone",
            "profile_identity_num",
            "profile_block_name",
            "profile_district_name",
            "profile_state_name"
        )
    )




}