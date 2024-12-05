package com.sarathi.surveymanager.search.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nudge.core.ARG_FROM_SECTION_SCREEN
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.SearchTab
import com.nudge.core.model.uiModel.ComplexSearchState
import com.nudge.core.model.uiModel.ItemType
import com.nudge.core.ui.events.SearchEvent
import com.nudge.core.value
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.SearchScreenUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchScreenUserCase: SearchScreenUseCase
) : BaseViewModel() {

    var surveyId: Int = 0
    var sectionId: Int = 0
    var taskId: Int = 0
    var activityConfigId: Int = 0
    var taskEntity: ActivityTaskEntity? = null
    var fromScreen: String = ARG_FROM_SECTION_SCREEN

    //first state whether the search is happening or not
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchTabFilter = mutableStateOf<SearchTab>(SearchTab.ALL_TAB)
    val searchTabFilter = _searchTabFilter

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _complexSearchStateList = mutableStateOf(mutableListOf<ComplexSearchState>())
    val complexSearchStateList: State<List<ComplexSearchState>> get() = _complexSearchStateList

    private var _filteredComplexSearchStateList = mutableStateListOf<ComplexSearchState>()
    val filteredComplexSearchStateList: SnapshotStateList<ComplexSearchState> get() = _filteredComplexSearchStateList

    fun initSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            taskEntity = searchScreenUserCase.getTaskUseCase.getTask(taskId)
            taskEntity?.let { task ->
                if (fromScreen == ARG_FROM_SECTION_SCREEN) {
                    val sectionList = searchScreenUserCase.getSectionListUseCase.invoke(surveyId)
                    sectionList.forEach { section ->
                        getComplexSearchStateForQuestionsForSection(section, task)
                    }
                } else {
                    val section =
                        searchScreenUserCase.getSectionListUseCase.invoke(surveyId, sectionId)
                    section?.let {
                        getComplexSearchStateForQuestionsForSection(it, task)
                    }
                }
            }
        }
    }

    private suspend fun getComplexSearchStateForQuestionsForSection(
        section: SectionUiModel,
        taskEntity: ActivityTaskEntity
    ) {
        val questionUiModel = searchScreenUserCase.fetchSurveyDataUseCase.invoke(
            surveyId = surveyId,
            sectionId = section.sectionId,
            subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
            activityConfigId = activityConfigId,
            referenceId = BLANK_STRING,
            grantId = NUMBER_ZERO,
            missionId = taskEntity?.missionId.value(DEFAULT_ID),
            activityId = taskEntity?.activityId.value(DEFAULT_ID)
        )
        _complexSearchStateList.value.addAll(questionUiModel.convertToComplexSearchState(section))
    }

    fun setPreviousScreenData(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        activityConfigId: Int,
        fromScreen: String
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.activityConfigId = activityConfigId
        this.fromScreen = fromScreen
    }

    private fun onSearchTextChange(queryTerm: String, tabFilter: SearchTab) {
        _searchText.value = queryTerm
        val filterList = ArrayList<ComplexSearchState>()
        when (tabFilter) {
            SearchTab.SECTION_INFORMATION_TAB -> {
                val mLocalList = complexSearchStateList.value.filter { it.isSectionSearchOnly }

                if (queryTerm.isNotEmpty()) {
                    mLocalList.forEach { complexSearchState ->
                        if (complexSearchState.sectionName.contains(queryTerm, true)) {
                            filterList.add(complexSearchState)
                        }
                    }
                } else {
                    _filteredComplexSearchStateList
                }
            }

            SearchTab.QUESTION_DATA_TAB -> {
                val mLocalList = complexSearchStateList.value
                if (queryTerm.isNotEmpty()) {
                    mLocalList.forEach { complexSearchState ->
                        if (complexSearchState.questionTitle.contains(queryTerm, true)) {
                            filterList.add(complexSearchState)
                        }
                    }
                } else {
                    _filteredComplexSearchStateList
                }
            }

            else -> {
                val mLocalList = complexSearchStateList.value
                if (queryTerm.isNotEmpty()) {
                    mLocalList.forEach { complexSearchState ->
                        if (complexSearchState.questionTitle.contains(queryTerm, true)
                            || complexSearchState.sectionName.contains(queryTerm, true)
                        ) {
                            filterList.add(complexSearchState)
                        }
                    }
                } else {
                    _filteredComplexSearchStateList
                }
            }
        }
        _filteredComplexSearchStateList.clear()
        _filteredComplexSearchStateList.addAll(filterList)
    }


    override fun <T> onEvent(event: T) {
        when (event) {
            is SearchEvent.SearchTabChanged -> {
                _searchTabFilter.value = event.searchTab
            }

            is SearchEvent.PerformComplexSearch -> {
                onSearchTextChange(event.searchTerm, event.tabFilter)
            }
        }

    }
}

private fun List<QuestionUiModel>.convertToComplexSearchState(section: SectionUiModel): List<ComplexSearchState> {
    val complexSearchStateList = ArrayList<ComplexSearchState>()
    section.let {
        val complexSearchState = ComplexSearchState(
            section.sectionId,
            itemType = ItemType.Section,
            sectionName = section.sectionName,
            questionTitle = BLANK_STRING,
            isSectionSearchOnly = true
        )
        complexSearchStateList.add(complexSearchState)

        this.forEach { questionUiModel ->
            val complexSearchStateForQuestion = ComplexSearchState(
                itemId = questionUiModel.questionId.value(),
                itemParentId = questionUiModel.sectionId,
                itemType = ItemType.Question,
                sectionName = section.sectionName,
                questionTitle = questionUiModel.questionDisplay.value(),
                isSectionSearchOnly = false
            )
            complexSearchStateList.add(complexSearchStateForQuestion)
        }
    }

    return complexSearchStateList
}

