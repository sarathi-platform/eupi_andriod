package com.patsurvey.nudge.activities.ui.progress.events

sealed class SelectionEvents {

    data class DownloadQuestionImages(val questionImageList: List<String>) : SelectionEvents()

    data class GetStateId(val result: (stateId: Int) -> Unit) : SelectionEvents()

}