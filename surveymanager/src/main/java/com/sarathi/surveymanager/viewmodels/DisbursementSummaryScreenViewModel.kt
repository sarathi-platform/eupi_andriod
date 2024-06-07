package com.sarathi.surveymanager.viewmodels

import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisbursementSummaryScreenViewModel @Inject constructor(
    private val grantConfigUseCase: GrantConfigUseCase
) : BaseViewModel() {
    private var surveyId: Int = 3
    private var sectionId: Int = 1
    private var taskId: Int = 1
    private var subjectType: String = "Vo"
    private var activityConfigId: Int = 0
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }


            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    //   saveSurveyAnswerUseCase.saveSurveyAnswer(event,subjectId)

                }
            }

        }
    }


    suspend fun getGrantConfig() {
        val grantConfigs = grantConfigUseCase.getGrantConfig(activityConfigId)


    }


    fun setPreviousScreenData(
        surveyId: Int, sectionId: Int, taskId: Int, subjectType: String, activityConfigId: Int
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.subjectType = subjectType
        this.activityConfigId = activityConfigId
    }

}