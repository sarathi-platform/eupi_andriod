package com.sarathi.surveymanager.viewmodels

import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DisbursementSummaryScreenViewModel @Inject constructor(
    private val grantConfigUseCase: GrantConfigUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase
) : BaseViewModel() {
    private var surveyId: Int = 3
    private var sectionId: Int = 1
    private var taskId: Int = 1
    private var subjectType: String = "Vo"
    private var activityConfigId: Int = 0

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initData()
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

    private fun initData() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            saveSurveyAnswerUseCase.getAllSaveAnswer(
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId
            )
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

    fun getReferenceId(): String {
        return UUID.randomUUID().toString()
    }

}