package com.sarathi.dataloadingmangement.util.event

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean) : LoaderEvent()
}
