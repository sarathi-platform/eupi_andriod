package com.sarathi.missionactivitytask.utils.event

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean) : LoaderEvent()
}
