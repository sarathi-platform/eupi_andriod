package com.nrlm.baselinesurvey.network

// Language and Caste API
const val SUBPATH_CONFIG_GET_LANGUAGE = "/read-api/config/language/get"
const val SUBPATH_CONFIG_GET_CASTE_LIST = "/read-api/config/caste/get"

// Login API
const val SUBPATH_AUTH_GENERATE_OTP = "/auth-api/user/generate-otp"
const val SUBPATH_AUTH_VALIDATE_OTP = "/auth-api/user/validate-otp"

// User View API
const val SUBPATH_USER_VIEW = "/read-api/user/view"

// DIDI List API
const val SUBPATH_GET_DIDI_LIST = "didi-service/upcm/view"
//    "/read-api/web/upcm/view"

// Survey API
//const val SUBPATH_FETCH_SURVEY_FROM_NETWORK = "/survey-engine/survey/view"
const val SUBPATH_FETCH_SURVEY_FROM_NETWORK = "/survey-engine/survey/getSurvey"
const val SUBPATH_SAVE_SURVEY_ANSWES = "/baseline-service/baseline/save"

const val SUBPATH_GET_SAVED_SURVEY = "/baseline-service/baseline/summary"
const val SUBPATH_GET_MISSION = "/mission-service/mission/view"
const val SUBPATH_GET_CASTE_LIST = "/read-api/config/caste/get"
const val GET_SECTION_STATUS = "/survey-service/getSectionStatus"

const val SUBPATH_LOGOUT = "/auth-api/user/logout"
const val SUBPATH_CONTENT_MANAGER = "/content-manager/content/getContent"
const val SUBPATH_SURVEY_ANSWERS = "/survey-service/getSurveyAnswers"
