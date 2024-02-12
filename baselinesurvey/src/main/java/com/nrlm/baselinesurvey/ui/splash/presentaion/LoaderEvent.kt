package com.nrlm.baselinesurvey.ui.splash.presentaion

sealed class LoaderEvent {
    data class UpdateLoaderState(val showLoader: Boolean): LoaderEvent()
}
