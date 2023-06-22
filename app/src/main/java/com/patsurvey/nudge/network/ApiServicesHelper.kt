package com.patsurvey.nudge.network

import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.ApiType.CAST_LIST_API
import com.patsurvey.nudge.utils.ApiType.DIDI_LIST_API
import com.patsurvey.nudge.utils.ApiType.DIDI_RANKING_API
import com.patsurvey.nudge.utils.ApiType.GENERATE_OTP_API
import com.patsurvey.nudge.utils.ApiType.PAT_CRP_QUESTION_API
import com.patsurvey.nudge.utils.ApiType.PAT_CRP_SURVEY_SUMMARY
import com.patsurvey.nudge.utils.ApiType.STEP_LIST_API
import com.patsurvey.nudge.utils.ApiType.TOLA_LIST_API
import com.patsurvey.nudge.utils.ApiType.VALIDATE_OTP_API
import com.patsurvey.nudge.utils.ApiType.VILLAGE_LIST_API
import com.patsurvey.nudge.utils.ApiType.WORK_FLOW_API

object ApiServicesHelper {

    const val SUBPATH_CONFIG_GET_LANGUAGE = "/read-api/config/language/get"
    const val SUBPATH_CONFIG_GET_CASTE_LIST = "/read-api/config/caste/get"

    const val SUBPATH_AUTH_GENERATE_OTP = "/auth-api/user/generate-otp"
    const val SUBPATH_AUTH_VALIDATE_OTP = "/auth-api/user/validate-otp"

    const val SUBPATH_USER_VILLAGE_LIST = "/read-api/user/view"
    const val SUBPATH_STEP_LIST = "/read-api/config/step/get"

    const val SUBPATH_GET_COHORT = "/write-api/cohort/view"
    const val SUBPATH_ADD_COHORT = "/write-api/cohort/add"
    const val SUBPATH_EDIT_COHORT = "/write-api/cohort/edit"
    const val SUBPATH_DELETE_COHORT = "/write-api/cohort/delete"

    const val SUBPATH_GET_DIDI = "/write-api/beneficiary/view"
    const val SUBPATH_ADD_DIDI = "/write-api/beneficiary/add"
    const val SUBPATH_EDIT_DIDI = "/write-api/beneficiary/edit"
    const val SUBPATH_DELETE_DIDI = "/write-api/beneficiary/delete"

    const val SUBPATH_ADD_WORKFLOW = "/write-api/workflow/add"
    const val SUBPATH_EDIT_WORKFLOW = "/write-api/workflow/edit"

    const val SUBPATH_GET_PAT_QUESTION = "/pat-api/pat/view"
    const val SUBPATH_PAT_SAVE_SUMMARY = "/pat-api/pat/save"
    const val SUBPATH_GET_PAT_SUMMARY = "/pat-api/pat/summary"

    const val SUBPATH_CALLBACK_STATUS = "/read-api/callback/status"

    fun getApiSubPath (api: ApiType): String {
        return when(api) {
            STEP_LIST_API -> SUBPATH_STEP_LIST
            TOLA_LIST_API -> SUBPATH_GET_COHORT
            DIDI_LIST_API -> SUBPATH_GET_DIDI
            DIDI_RANKING_API -> SUBPATH_GET_DIDI
            PAT_CRP_QUESTION_API -> SUBPATH_GET_PAT_QUESTION
            PAT_CRP_SURVEY_SUMMARY -> SUBPATH_GET_PAT_SUMMARY
            WORK_FLOW_API -> SUBPATH_EDIT_WORKFLOW
            VILLAGE_LIST_API -> SUBPATH_USER_VILLAGE_LIST
            VALIDATE_OTP_API -> SUBPATH_AUTH_VALIDATE_OTP
            GENERATE_OTP_API -> SUBPATH_AUTH_GENERATE_OTP
            CAST_LIST_API -> SUBPATH_CONFIG_GET_CASTE_LIST
        }
    }

}