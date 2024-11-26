package com.nudge.core.enums

import com.nudge.core.utils.GET_SECTION_STATUS
import com.nudge.core.utils.SUBPATH_CONFIG_GET_LANGUAGE
import com.nudge.core.utils.SUBPATH_FETCH_LIVELIHOOD_OPTION
import com.nudge.core.utils.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.nudge.core.utils.SUBPATH_GET_ASSETS_JOURNAL_DETAILS
import com.nudge.core.utils.SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
import com.nudge.core.utils.SUBPATH_GET_DIDI_LIST
import com.nudge.core.utils.SUBPATH_GET_FORM_DETAILS
import com.nudge.core.utils.SUBPATH_GET_LIVELIHOOD_CONFIG
import com.nudge.core.utils.SUBPATH_GET_LIVELIHOOD_SAVE_EVENT
import com.nudge.core.utils.SUBPATH_GET_MONEY_JOURNAL_DETAILS
import com.nudge.core.utils.SUBPATH_GET_SMALL_GROUP_MAPPING
import com.nudge.core.utils.SUBPATH_SURVEY_ANSWERS
import com.nudge.core.utils.SUBPATH_USER_VIEW
import com.nudge.core.utils.SUB_PATH_CONTENT_MANAGER
import com.nudge.core.utils.SUB_PATH_GET_ACTIVITY_DETAILS
import com.nudge.core.utils.SUB_PATH_GET_MISSION_DETAILS

enum class ApiStatus {
    INPROGRESS, SUCCESS, FAILED
}

enum class ApiDetails(
    val endPoint: String,
    callScreen: List<CallScreen>,
    isCalledInPullToRefresh: Boolean = true
) {

    LANGUAGE_API(SUBPATH_CONFIG_GET_LANGUAGE, listOf(CallScreen.MISSION_SCREEN), false),
    USER_VIEW(SUBPATH_USER_VIEW, listOf(CallScreen.MISSION_SCREEN)),
    MISSION_DETAILS(SUB_PATH_GET_MISSION_DETAILS, listOf(CallScreen.MISSION_SCREEN)),
    CONTENT_V2(SUB_PATH_CONTENT_MANAGER, listOf(CallScreen.MISSION_SCREEN)),
    MONEY_JOURNAL_DETAILS(
        SUBPATH_GET_MONEY_JOURNAL_DETAILS,
        listOf(CallScreen.MISSION_SCREEN),
        false
    ),

    ACTIVITY_DETAILS(
        SUB_PATH_GET_ACTIVITY_DETAILS,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN)
    ),
    SURVEY_DETAILS(
        SUBPATH_FETCH_SURVEY_FROM_NETWORK,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN)
    ),
    SURVEY_ANSWERS(
        SUBPATH_SURVEY_ANSWERS,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN),
        false
    ),
    SURVEY_SECTIONS_STATUS(
        GET_SECTION_STATUS,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN),
        false
    ),
    FORM_DETAILS(
        SUBPATH_GET_FORM_DETAILS,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN),
        false
    ),
    LIVELIHOOD_CONFIG_DETAILS(
        SUBPATH_GET_LIVELIHOOD_CONFIG,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN, CallScreen.DATA_TAB_SCREEN)
    ),
    DIDI_LIVELIHOOD_MAPPING(
        SUBPATH_FETCH_LIVELIHOOD_OPTION,
        listOf(CallScreen.ACTIVITY_SCREEN, CallScreen.TASK_SCREEN),
        false
    ),

    UPCM_DIDI_MAPPING(
        SUBPATH_GET_DIDI_LIST,
        listOf(CallScreen.DATA_TAB_SCREEN, CallScreen.DIDI_TAB_SCREEN)
    ),
    SMALL_GROUP_DIDI_MAPPING(SUBPATH_GET_SMALL_GROUP_MAPPING, listOf(CallScreen.DIDI_TAB_SCREEN)),
    SMALL_GROUP_ATTENDANCE_DETAILS(
        SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK,
        listOf(CallScreen.DIDI_TAB_SCREEN),
        false
    ),

    ASSET_JOURNAL_DETAILS(
        SUBPATH_GET_ASSETS_JOURNAL_DETAILS,
        listOf(CallScreen.DATA_TAB_SCREEN),
        false
    ),
    LIVELIHOOD_EVENTS(SUBPATH_GET_LIVELIHOOD_SAVE_EVENT, listOf(CallScreen.DATA_TAB_SCREEN), false)


}


enum class CallScreen() {

    MISSION_SCREEN,
    ACTIVITY_SCREEN,
    TASK_SCREEN,
    DIDI_TAB_SCREEN,
    DATA_TAB_SCREEN

}