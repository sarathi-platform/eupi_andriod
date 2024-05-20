package com.nrlm.baselinesurvey.ui.common_components.common_events

sealed class FetchDataEvent {
    object FetchUserDetailEvent: FetchDataEvent()
    data class FetchBeneficiaryListEvent(val userId: Int): FetchDataEvent()
//    data class FetchSurveyEvent(): FetchDataEvent()
}
