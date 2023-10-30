package com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.HUSBAND_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.DidiEntity
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.SurveyeeScreenUseCase
import com.nrlm.baselinesurvey.utils.LoaderState
import com.nrlm.baselinesurvey.utils.SurveyState
import com.nrlm.baselinesurvey.utils.SurveyeeCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyeeScreenViewModel @Inject constructor(
    private val surveyeeScreenUseCase: SurveyeeScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _didiList = mutableStateOf<List<DidiEntity>>(listOf())
    val didiList: State<List<DidiEntity>> get() = _didiList

    private val _surveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val surveyeeListState: State<List<SurveyeeCardState>> get() = _surveyeeListState


    fun init() {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value = surveyeeScreenUseCase.getSurveyeeListUseCase.invoke()
            didiList.value.forEach { item ->
                val surveyeeState = SurveyeeCardState(
                    didiDetails = item,
                    imagePath = item.localPath,
                    subtitle = item.guardianName,
                    address = item.address + "," + item.cohortName,
                    surveyState = SurveyState.NOT_STARTED
                )
                _surveyeeListState.value.add(surveyeeState)
            }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

        }

    }


    override fun <T> onEvent(event: T) {
       when (event) {
           is LoaderEvent.UpdateLoaderState -> {
               _loaderState.value = _loaderState.value.copy(
                   isLoaderVisible = event.showLoader
               )
           }
       }
    }

}