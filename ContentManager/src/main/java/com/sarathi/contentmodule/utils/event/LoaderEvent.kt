package com.sarathi.contentmodule.utils.event

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean) : LoaderEvent()
}
