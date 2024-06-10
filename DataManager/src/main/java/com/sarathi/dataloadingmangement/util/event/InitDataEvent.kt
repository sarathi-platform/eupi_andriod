package com.sarathi.dataloadingmangement.util.event


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitContentScreenState(val matId: Int, val contentCategory: Int) : InitDataEvent()
}
