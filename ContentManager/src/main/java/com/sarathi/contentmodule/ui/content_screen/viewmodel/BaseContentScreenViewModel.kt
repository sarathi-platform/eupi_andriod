package com.sarathi.contentmodule.ui.content_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
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
    private val fetchContentUseCase: FetchContentUseCase,
    private val downloaderManager: DownloaderManager
) :
    BaseViewModel() {
    private val _contentList = mutableStateOf<List<Content>>(emptyList())
    val contentList: State<List<Content>> get() = _contentList
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                initContentScreen()
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
            _contentList.value = fetchContentUseCase.getContentData()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

}