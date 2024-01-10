package com.nrlm.baselinesurvey.ui.search.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.common_components.SearchTab
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SearchScreenViewModel @Inject constructor(): BaseViewModel() {

    //first state whether the search is happening or not
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchTabFilter = mutableStateOf<SearchTab>(SearchTab.ALL_TAB)
    val searchTabFilter = _searchTabFilter

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchItems = MutableStateFlow(sampleSearchStates)
    val searchItems = searchText
        .combine(_searchItems) { text, searchItems ->
            if (text.isBlank()) {
                emptyList<ComplexSearchState>()
            }
                _searchItems.value.filter {
                    (it.sectionName.lowercase().contains(text.trim().lowercase(), true)
                            || it.questionTitle.lowercase().contains(text.trim().lowercase(), true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = emptyList<ComplexSearchState>())



    fun onSearchTextChange(text: String, tabFilter: SearchTab) {
        _searchText.value = text
        _searchTabFilter.value = tabFilter
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
    val sectionName: String,
    val questionTitle: String,
    val isSectionSearchOnly: Boolean = false
)

val sampleSearchStates = listOf<ComplexSearchState>(
    ComplexSearchState("Financial Inclusion", "Do you have a bank account?"),
    ComplexSearchState("Social Inclusion", "Are you a member of an SHG?"),
    ComplexSearchState("Social Inclusion", "Do you attend SHG meetings regularly?"),
    ComplexSearchState("Social Inclusion", "", true)
)
