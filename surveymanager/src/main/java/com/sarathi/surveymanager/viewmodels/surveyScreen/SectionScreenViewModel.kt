package com.sarathi.surveymanager.viewmodels.surveyScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SectionScreenViewModel @Inject constructor(
    val getSectionListUseCase: GetSectionListUseCase
) : BaseViewModel() {

    private var missionId: Int = 0
    private var activityId: Int = 0
    private var surveyId: Int = 0
    private var taskId: Int = 0
    private var subjectType: String = ""
    private var activityConfigId: Int = 0

    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList

    private val _sectionList = mutableStateOf<List<SectionUiModel>>(mutableListOf())
    val sectionList: State<List<SectionUiModel>> get() = _sectionList

    private val _sectionStatusMap: MutableState<MutableMap<Int, String>> =
        mutableStateOf(mutableMapOf())
    val sectionStatusMap: State<Map<Int, String>> get() = _sectionStatusMap

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataStateWithCallBack -> {
                initSectionScreen(callBack = event.callBack)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initSectionScreen(callBack: () -> Unit) {
        ioViewModelScope {
            _sectionList.value = getSectionListUseCase.invoke(surveyId)
            _sectionStatusMap.value =
                getSectionListUseCase.getSectionStatusMap(missionId, surveyId, taskId)
                    .toMutableMap()
            withContext(mainDispatcher) {
                callBack()
            }
        }
    }

    override fun refreshData() {
        super.refreshData()

    }

    fun setSurveyDetails(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        taskId: Int,
        subjectType: String,
        activityConfigId: Int,
    ) {
        this.missionId = missionId
        this.activityId = activityId
        this.surveyId = surveyId
        this.taskId = taskId
        this.subjectType = subjectType
        this.activityConfigId = activityConfigId
    }

    fun handleMediaContentClick(
        contentKey: String, callNavigation: (
            contentType: String,
            contentTitle: String
        ) -> Unit
    ) {
        val content = contentList.value.find { it.contentKey == contentKey }
        content?.let {
            callNavigation(content.contentType, content.contentValue)
        }

    }

}