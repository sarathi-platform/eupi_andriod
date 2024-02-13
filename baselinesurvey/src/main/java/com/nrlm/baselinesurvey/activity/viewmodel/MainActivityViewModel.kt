package com.nrlm.baselinesurvey.activity.viewmodel

import com.nrlm.baselinesurvey.activity.domain.use_case.MainActivityUseCase
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nudge.syncmanager.database.dao.EventsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val mainActivityUseCase: MainActivityUseCase,
    val eventsDao: EventsDao
): BaseViewModel() {


    fun isLoggedIn() = mainActivityUseCase.isLoggedInUseCase.invoke()

    override fun <T> onEvent(event: T) {

    }


}