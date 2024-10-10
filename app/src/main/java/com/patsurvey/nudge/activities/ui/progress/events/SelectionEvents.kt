package com.patsurvey.nudge.activities.ui.progress.events

sealed class SelectionEvents {

    data class DownloadQuestionImages(val questionImageList: List<String>) : SelectionEvents()

    data class GetStateId(val result: (stateId: Int) -> Unit) : SelectionEvents()

    data class UpdateSelectedVillage(val selectedIndex: Int) : SelectionEvents()

    data class IsUserBpc(val result: (isUserBpc: Boolean) -> Unit) : SelectionEvents()

    data class GetAppLanguage(val result: (appLanguage: String) -> Unit) : SelectionEvents()

    object Logout : SelectionEvents()

}