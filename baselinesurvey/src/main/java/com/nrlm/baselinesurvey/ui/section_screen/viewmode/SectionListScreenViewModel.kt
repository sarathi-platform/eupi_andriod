package com.nrlm.baselinesurvey.ui.section_screen.viewmode

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.common_components.common_events.ApiStatusEvent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.SectionListScreenUseCase
import com.nrlm.baselinesurvey.ui.section_screen.presentation.SectionScreenEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.findItemBySectionId
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SectionListScreenViewModel @Inject constructor(
    val sectionScreenUseCase: SectionListScreenUseCase,
    private val fetchDataUseCase: FetchDataUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionsList = mutableStateOf<List<SectionListItem>>(listOf())

    val sectionsList: State<List<SectionListItem>> get() = _sectionsList

    private val _sectionItemStateList = mutableStateOf(mutableListOf<SectionState>())
    val sectionItemStateList: State<List<SectionState>> get() = _sectionItemStateList
    val didiName = mutableStateOf("")
    var didiDetails: SurveyeeEntity? = null

    val sampleVideoPath = "https://nudgetrainingdata.blob.core.windows.net/recordings/Videos/M6ParticipatoryWealthRanking.mp4"

    val allSessionCompleted = mutableStateOf(false)

    val isSurveyCompletedForDidi = mutableStateOf(true)
    var didiId: Int = 0
    var surveyId: Int = 0
    fun init(didiId: Int, surveyId: Int) {
        this.surveyId = surveyId
        this.didiId = didiId
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val selectedLanguageId =
                    sectionScreenUseCase.getSectionListUseCase.getSelectedLanguage()
                if (_sectionsList.value.isEmpty()) {
                    _sectionsList.value = sectionScreenUseCase.getSectionListUseCase.invoke(
                        didiId,
                        surveyId,
                        selectedLanguageId
                    )
                    val sectionProgressForDidi =
                        sectionScreenUseCase.getSectionProgressForDidiUseCase.invoke(
                            didiId,
                            surveyId,
                            selectedLanguageId
                        )
                    sectionsList.value.sortedBy { it.sectionOrder }
                        .forEachIndexed { index, section ->
                            val sectionState = SectionState(
                                section,
                                sectionStatus = if (sectionProgressForDidi.map { it.sectionId }
                                        .contains(section.sectionId)) {
                                    SectionStatus.getSectionStatusFromOrdinal(
                                        sectionProgressForDidi.findItemBySectionId(section.sectionId).sectionStatus
                                    )
                                } else {
                                    SectionStatus.INPROGRESS
                                }
                            )
                            _sectionItemStateList.value.add(sectionState)
                            val mDidiDetails = sectionScreenUseCase.getSurvyeDetails.getSurveyeDetails(didiId = didiId)
                            didiDetails = mDidiDetails
                            didiName.value = mDidiDetails.didiName
//                            isSurveyCompletedForDidi.value = didiDetails.surveyStatus != SurveyState.COMPLETED.ordinal
                            allSessionCompleted.value = isAllSessionCompleted()

                        }

                }
                withContext(Dispatchers.Main) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            } catch (ex: Exception) {
                BaselineLogger.e("SectionListScreenViewModel", "init", ex)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

        }


        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.NOT_STARTED else SectionStatus.INPROGRESS))
        }*/

        /*sectionsList.value.forEach { section ->
            _sectionItemStateList.add(SectionState(section, if (section.sectionId == 2) SectionStatus.INPROGRESS else SectionStatus.COMPLETED))
        }*/


    }


    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is SectionScreenEvent.UpdateSubjectStatus -> {
                CoroutineScope(Dispatchers.IO).launch {
                    sectionScreenUseCase.updateSubjectStatusUseCase.invoke(
                        didiId = event.didiId,
                        surveyState = event.surveyState
                    )
                    // TODO Add Update Subject Status Code
//                    onEvent(EventWriterEvents.UpdateSubjectSurveyStatus())
                }
            }

            is SectionScreenEvent.UpdateTaskStatus -> {
                CoroutineScope(Dispatchers.IO).launch {
                    sectionScreenUseCase.updateTaskStatusUseCase.invoke(
                        didiId = event.didiId,
                        surveyState = event.surveyState
                    )
                    onEvent(
                        EventWriterEvents.UpdateTaskStatusEvent(
                            event.didiId,
                            event.surveyState
                        )
                    )
                }
            }

            is EventWriterEvents.UpdateTaskStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val activityForSubjectDto =
                        eventWriterHelperImpl.getActivityFromSubjectId(event.subjectId)

                    if (activityForSubjectDto != null) {
                        eventWriterHelperImpl.markTaskCompleted(
                            activityForSubjectDto.missionId,
                            activityForSubjectDto.activityId,
                            activityForSubjectDto.taskId,
                            event.status
                        )
                    }

                    val updateTaskStatusEvent = eventWriterHelperImpl.createTaskStatusUpdateEvent(
                        subjectId = event.subjectId,
                        sectionStatus = event.status
                    )
                    sectionScreenUseCase.eventsWriterUseCase.invoke(
                        updateTaskStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }
            is ApiStatusEvent.showApiStatus -> {
                if (event.errorCode == 200) {
                    init(didiId, surveyId)
                    showCustomToast(
                        BaselineCore.getAppContext(), BaselineCore.getAppContext().getString(
                        R.string.fetched_successfully))
                } else {
                    showCustomToast(
                        BaselineCore.getAppContext(),
                        event.message
                    )
                }
            }
        }
    }

    fun getSurveeDetail(didiId: Int): SurveyeeEntity {
        return sectionScreenUseCase.getSurvyeDetails.getSurveyeDetails(didiId = didiId)
    }

    private fun isAllSessionCompleted(): Boolean {
        var isFlag = true
        _sectionItemStateList.value.forEach { sectionState ->
            if (sectionState.sectionStatus != SectionStatus.COMPLETED) {
                return false
            }
        }
        return true
    }
    fun getContentData(
        contents: List<ContentEntity?>?,
        contentType: String
    ): ContentEntity? {
        contents?.let { contentsData ->
            for (content in contentsData) {
                if (content?.contentType.equals(contentType, true)) {
                    return content!!
                }
            }
        }
        return null
    }

    fun refreshData() {
        refreshData(fetchDataUseCase)
    }

    fun close() {
        _loaderState.value = LoaderState()
        _sectionsList.value = listOf()
        _sectionItemStateList.value = mutableListOf<SectionState>()
    }
}
