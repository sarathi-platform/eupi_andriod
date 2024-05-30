package com.sarathi.contentmodule.ui.content_detail_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.contentmodule.ui.viewmodel.BaseViewModel
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val fetchContentUseCase: FetchContentUseCase
) : BaseViewModel() {
    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList
    var contentCount = mutableStateOf(0)
    private val _filterContentList = mutableStateOf<List<Content>>(emptyList())
    var filterSelected = mutableStateOf(false)
    val filterContentList: State<List<Content>> get() = _filterContentList
    var filterContentMap by mutableStateOf(mapOf<String, List<Content>>())

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initContentScreen()
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initContentScreen() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            contentCount.value = fetchContentUseCase.getContentCount()
            _contentList.value = fetchContentUseCase.getContentData()
            _filterContentList.value = fetchContentUseCase.getContentData()
            filterContentMap = contentList.value.groupBy { it.contentType }

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun performSearchQuery(
        queryTerm: String, isFilterApplied: Boolean, fromScreen: String
    ) {
        val filteredList = ArrayList<Content>()
        if (queryTerm.isNotEmpty()) {
            contentList.value.forEach { content ->
                if (content.contentName.lowercase().contains(queryTerm.lowercase())) {
                    filteredList.add(content)
                }
            }
        } else {
            filteredList.addAll(contentList.value)
        }
        _filterContentList.value = filteredList
    }

    fun isFilePathExists(filePath: String): Boolean {
        return fetchContentUseCase.isFilePathExists(filePath)
    }

}