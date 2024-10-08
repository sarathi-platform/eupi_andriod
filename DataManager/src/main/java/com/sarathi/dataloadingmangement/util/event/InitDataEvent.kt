package com.sarathi.dataloadingmangement.util.event


sealed class InitDataEvent {
    object InitDataState : InitDataEvent()
    data class InitContentScreenState(val matId: Int, val contentCategory: Int) : InitDataEvent()
    data class InitDataSummaryScreenState(val subjectId: Int) : InitDataEvent()
    data class InitAddEventState(val subjectId: Int, val transactionId: String) : InitDataEvent()
    data class InitEditHistoryState(val transactionId: String) : InitDataEvent()

    data class InitDataStateWithCallBack(val callBack: () -> Unit) : InitDataEvent()

    object InitChangeUserState : InitDataEvent()
}
