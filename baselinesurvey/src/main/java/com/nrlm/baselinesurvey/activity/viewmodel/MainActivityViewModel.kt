package com.nrlm.baselinesurvey.activity.viewmodel

import com.nrlm.baselinesurvey.activity.domain.use_case.MainActivityUseCase
import com.nrlm.baselinesurvey.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val mainActivityUseCase: MainActivityUseCase,
//    val eventsDao: com.nudge.core.database.dao.EventsDao
): BaseViewModel() {


    fun isLoggedIn() = mainActivityUseCase.isLoggedInUseCase.invoke()

    override fun <T> onEvent(event: T) {
        when (event) {
            /*is TestEvent.SampleEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    eventsDao.insert(event = event.events)
                }
            }*/
        }
    }


}