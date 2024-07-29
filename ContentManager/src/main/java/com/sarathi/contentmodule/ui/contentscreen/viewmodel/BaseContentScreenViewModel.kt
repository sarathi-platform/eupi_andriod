package com.sarathi.contentmodule.ui.contentscreen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.contentmodule.constants.Constants.CONTENT_LIMIT_DATA
import com.sarathi.contentmodule.ui.contentscreen.domain.usecase.FetchContentUseCase
import com.sarathi.contentmodule.ui.viewmodel.BaseViewModel
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
class BaseContentScreenViewModel @Inject constructor(
    private val fetchContentUseCase: FetchContentUseCase
) :
    BaseViewModel() {
    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList
    var contentCount = mutableStateOf(0)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitContentScreenState -> {
                initContentScreen(event.matId, event.contentCategory)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    private fun initContentScreen(matId: Int, contentCategory: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            contentCount.value = fetchContentUseCase.getContentCount(matId, contentCategory)
            _contentList.value = fetchContentUseCase.getLimitedContentData(
                CONTENT_LIMIT_DATA,
                matId,
                contentCategory
            )
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

}