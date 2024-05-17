package com.sarathi.missionactivitytask.utils

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean) : LoaderEvent()
}
