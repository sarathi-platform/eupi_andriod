package com.sarathi.surveymanager.viewmodels.surveyScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SectionScreenViewModel @Inject constructor(
    private val getSectionListUseCase: GetSectionListUseCase,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val getActivityUseCase: GetActivityUseCase,

    ) : BaseViewModel() {

    private var missionId: Int = 0
    private var activityId: Int = 0
    private var surveyId: Int = 0
    private var taskId: Int = 0
    private var subjectType: String = BLANK_STRING
    var activityConfigId: Int = 0

    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList

    private val _sectionList = mutableStateOf<List<SectionUiModel>>(mutableListOf())
    val sectionList: State<List<SectionUiModel>> get() = _sectionList

    private val _sectionStatusMap: MutableState<MutableMap<Int, String>> =
        mutableStateOf(mutableMapOf())
    val sectionStatusMap: State<Map<Int, String>> get() = _sectionStatusMap

    val isButtonEnable = mutableStateOf<Boolean>(false)
    var isActivityCompleted = mutableStateOf(false)

    val buttonVisibilityKey: MutableState<Boolean> =
        mutableStateOf(sectionStatusMap.value.all { it.value == SurveyStatusEnum.COMPLETED.name })

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
            isActivityCompleted()
        }
    }

    fun isActivityCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isActivityCompleted.value = getActivityUseCase.isAllActivityCompleted(
                missionId = missionId,
                activityId = activityId
            )
            checkButtonValidation()
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

    fun updateTaskStatus(taskId: Int) {
        ioViewModelScope {
            val surveyEntity = getSectionListUseCase.getSurveyEntity(surveyId)
            surveyEntity?.let { survey ->
                taskStatusUseCase.markTaskCompleted(taskId)
                val task = eventWriterUseCase.getTaskEntity(taskId)
                eventWriterUseCase.updateTaskStatus(task, survey.surveyName, subjectType)
            }

        }
    }

    fun checkButtonValidation() {
//
        buttonVisibilityKey.value =
            sectionStatusMap.value.all { it.value == SurveyStatusEnum.COMPLETED.name }
        isButtonEnable.value =
            sectionList.value.size == sectionStatusMap.value.size  && !isActivityCompleted.value && sectionStatusMap.value.values.toList()
                .all { it == SurveyStatusEnum.COMPLETED.name }

    }
    fun getContentData(
        contents: List<ContentList?>?,
        contentType: String
    ): ContentList? {
        contents?.let { contentsData ->
            for (content in contentsData) {
                if (content?.contentType.equals(contentType, true)) {
                    return content!!
                }
            }
        }
        return null
    }
    fun isFilePathExists(filePath: String): Boolean {
        return fetchContentUseCase.isFilePathExists(filePath)
    }


}