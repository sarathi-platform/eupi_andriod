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
const val SUBPATH_GET_DIDI_LIST = "/read-api/web/upcm/view"

// Survey API
const val SUBPATH_FETCH_SURVEY_FROM_NETWORK = "/survey-engine/survey/view"
const val SUBPATH_SAVE_SURVEY_ANSWES = "/baseline-service/baseline/save"

const val SUBPATH_GET_SAVED_SURVEY = "/baseline-service/baseline/summary"
const val SUBPATH_GET_MISSION = "/baseline-service/mission/view"