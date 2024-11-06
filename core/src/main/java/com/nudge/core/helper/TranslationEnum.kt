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
            "date_range_picker_label_text",
            "all_events",
            "last_events",
            "show_less",
            "show_more",
            "view_edit_history",
            "event",
            "amount",
            "asset",
            "add_event"
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
    ActivitySelectTaskScreen(
        "ActivitySelectTaskScreen",
        listOf("not_available", "activity_completed_unable_to_edit")
    ),
    SubmitPhysicalFormScreen("SubmitPhysicalFormScreen", listOf("go_back")),
    LivelihoodTaskCard(
        "LivelihoodTaskCard",
        listOf("not_available", "in_progress", "task_view", "edit", "continue_text")
    ),
    TaskCard(
        "TaskCard",
        listOf(
            "not_available",
            "in_progress",
            "task_view",
            "continue_text",
            "activity_completed_unable_to_edit"
        )
    ),
    FormSummaryDialog("FormSummaryDialog", listOf("mode", "nature", "amount")),

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
    ActivityScreen(
        "ActivityScreen",
        listOf(
            "complete_mission",
            "not_be_able_to_make_changes_after_completing_this_mission",
            "cancel",
            "ok"
        )
    ),
    LivelihoodTaskScreen("LivelihoodTaskScreen", listOf()),
    MissionScreen(
        "MissionScreen",
        listOf(
            "are_you_sure",
            "do_you_want_to_exit_the_app",
            "exit",
            "cancel",
            "data_not_Loaded",
            "ok",
            "refresh_failed_please_try_again",
            "search",
            "activities_completed",
            "start"
        )
    ),
    TaskScreen(
        "TaskScreen",
        listOf(
            "no_small_group_assgned_label",
            "small_group_filter_label",
            "since_you_have_completed_all_the_tasks_please_complete_the_activity",
            "on_completing_the_activity_you_will_not_be_able_to_edit_the_details",
            "complete_activity",
            "not_be_able_to_make_changes_after_completing_this_activity",
            "cancel",
            "ok",
            "filter_item_count_label"
        )
    ),

    NoScreen("NoScreen", emptyList())

}