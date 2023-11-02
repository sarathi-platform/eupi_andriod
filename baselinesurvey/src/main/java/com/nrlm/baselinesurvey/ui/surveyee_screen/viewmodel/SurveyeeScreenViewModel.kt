package com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nrlm.baselinesurvey.NO_TOLA_TITLE
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.SurveyeeScreenUseCase
import com.nrlm.baselinesurvey.utils.LoaderState
import com.nrlm.baselinesurvey.utils.SurveyState
import com.nrlm.baselinesurvey.utils.SurveyeeCardState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyeeScreenViewModel @Inject constructor(
    private val surveyeeScreenUseCase: SurveyeeScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _surveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val surveyeeListState: State<List<SurveyeeCardState>> get() = _surveyeeListState

    private var _filteredSurveyeeListState = mutableStateOf(mutableListOf<SurveyeeCardState>())
    val filteredSurveyeeListState: State<List<SurveyeeCardState>> get() = _filteredSurveyeeListState

    var tolaMapList by mutableStateOf(mapOf<String, List<SurveyeeCardState>>())

    private var _tolaMapSurveyeeListState = mutableStateOf(mapOf<String, List<SurveyeeCardState>>())
    val tolaMapSurveyeeListState: State<Map<String, List<SurveyeeCardState>>> get() = _tolaMapSurveyeeListState


    fun init() {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            surveyeeScreenUseCase.getSurveyeeListUseCase.invoke().onEach { items ->
                items.forEach { surveyeeEntity ->
                    val surveyeeState = SurveyeeCardState(
                        surveyeeDetails = surveyeeEntity,
                        imagePath = surveyeeEntity.crpImageName,
                        subtitle = surveyeeEntity.dadaName,
                        address = getSurveyeeAddress(surveyeeEntity),
                        surveyState = SurveyState.getStatusFromOrdinal(surveyeeEntity.surveyStatus)
                    )
                    _surveyeeListState.value.add(surveyeeState)
                }
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
           is SearchEvent.PerformSearch -> {
               performSearchQuery(event.searchTerm, event.isFilterApplied)
           }
           is SearchEvent.FilterList -> {
               filterList()
           }
       }
    }

    override fun performSearchQuery(queryTerm: String, isFilterApplied: Boolean) {
        if (!isFilterApplied) {
            _filteredSurveyeeListState.value = if (queryTerm.isNotEmpty()) {
                val filteredList = ArrayList<SurveyeeCardState>()
                surveyeeListState.value.forEach { surveyeeCardState ->
                    if (surveyeeCardState.surveyeeDetails.didiName.lowercase().contains(queryTerm.lowercase())) {
                        filteredList.add(surveyeeCardState)
                    }
                }
                filteredList
            } else {
                _surveyeeListState.value
            }
        } else {
            if (queryTerm.isNotEmpty()) {
                val mFilterMap = mutableMapOf<String, MutableList<SurveyeeCardState>>()
                tolaMapList.keys.forEach {  key ->
                    val mSurveyeeCardState = ArrayList<SurveyeeCardState>()
                    tolaMapList[key]?.forEach { surveyeeCardState ->
                        if (surveyeeCardState.surveyeeDetails.didiName.lowercase().contains(queryTerm.lowercase())) {
                            mSurveyeeCardState.add(surveyeeCardState)
                        }
                    }
                    if (mSurveyeeCardState.isNotEmpty())
                        mFilterMap[key] = mSurveyeeCardState
                }
                _tolaMapSurveyeeListState.value = mFilterMap
            } else {
                _surveyeeListState.value
            }
        }
    }

    override fun filterList() {
        val map = mutableMapOf<String, MutableList<SurveyeeCardState>>()
        surveyeeListState.value.forEachIndexed { index, surveyeeCardState ->
            if (map.contains(surveyeeCardState.surveyeeDetails.cohortName)) {
                map[surveyeeCardState.surveyeeDetails.cohortName]?.add(surveyeeCardState)
            } else {
                map[surveyeeCardState.surveyeeDetails.cohortName] = mutableListOf(surveyeeCardState)
            }
        }
        tolaMapList = map
        _tolaMapSurveyeeListState.value = map
    }


    fun getSurveyeeAddress(surveyeeEntity: SurveyeeEntity): String {
        return if (surveyeeEntity.cohortName.equals(NO_TOLA_TITLE, true))
            surveyeeEntity.houseNo + "," + surveyeeEntity.cohortName
        else
            surveyeeEntity.houseNo + "," + surveyeeEntity.villageName
    }

}