package com.nrlm.baselinesurvey.ui.search.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.Constants.ItemType
import com.nrlm.baselinesurvey.ui.common_components.SearchTab
import com.nrlm.baselinesurvey.ui.search.use_case.SearchScreenUseCase
import com.nrlm.baselinesurvey.utils.convertToComplexSearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchScreenUserCase: SearchScreenUseCase
): BaseViewModel() {

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

    fun initSearch(surveyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedLanguageId =
                searchScreenUserCase.getSectionListForSurveyUseCase.getSelectedLanguage()
            val sectionDetail = searchScreenUserCase.getSectionListForSurveyUseCase.invoke(surveyId, selectedLanguageId)
            val mComplexSearchStateList = sectionDetail.convertToComplexSearchState()
            if (complexSearchStateList.value.isNotEmpty()) {
                _complexSearchStateList.value.clear()
            }
            _complexSearchStateList.value.addAll(mComplexSearchStateList)
        }
    }

    fun onSearchTextChange(queryTerm: String, tabFilter: SearchTab) {
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
                            || complexSearchState.sectionName.contains(queryTerm, true)) {
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

    /*fun onToogleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }*/

    override fun <T> onEvent(event: T) {
        /*when (event) {
            is SearchEvent.SearchTabChanged -> {
                searchItems = searchItems.combine(_searchItems) {
                        text, searchItems ->
                    when (searchTabFilter.value) {
                        is SearchTab.ALL_TAB -> {
                            searchItems
                        }
                        is SearchTab.QUESTION_DATA_TAB -> {searchItems.filter { !it.isSectionSearchOnly }}
                        is SearchTab.SECTION_INFORMATION_TAB -> {
                            searchItems.filter { it.isSectionSearchOnly }
                        }

                        else -> { searchItems}
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), initialValue = emptyList<ComplexSearchState>())
            }
        }*/

    }
}

data class ComplexSearchState(
    val itemId: Int,
    val itemParentId: Int = -1,
    val itemType: ItemType,
    val sectionName: String,
    val questionTitle: String,
    val isSectionSearchOnly: Boolean = false
)

/*val sampleSearchStates = listOf<ComplexSearchState>(
    ComplexSearchState("Financial Inclusion", "Do you have a bank account?"),
    ComplexSearchState("Social Inclusion", "Are you a member of an SHG?"),
    ComplexSearchState("Social Inclusion", "Do you attend SHG meetings regularly?"),
    ComplexSearchState("Social Inclusion", "", true),
    ComplexSearchState("Food Security", "Did everyone in your family have at least 2 meals per day in the last 1 month?"),
    ComplexSearchState("Food Security", "Did everyone in your family have at least 2 meals per day in the last 12 month?")
)*/
