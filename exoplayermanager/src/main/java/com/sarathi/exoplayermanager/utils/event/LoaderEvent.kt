package com.sarathi.exoplayermanager.utils.event

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean) : LoaderEvent()
}
