package com.sarathi.contentmodule.media

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.contentmodule.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    private val fetchContentUseCase: FetchContentUseCase
) : BaseViewModel() {
    var contentUrl = mutableStateOf("")

    private suspend fun getContentValue(contentKey: String): String {
        return fetchContentUseCase.getContentValue(contentKey)
    }

    fun initData(contentKey: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            contentUrl.value = getContentValue(contentKey)
        }
    }

    fun getFilePathUri(filePath: String): Uri? {
        return fetchContentUseCase.getFilePathUri(filePath)
    }

    fun getFilePath(filePath: String): File? {
        return fetchContentUseCase.getFilePath(filePath)
    }

    override fun <T> onEvent(event: T) {
        // Use to create and update event on screen
    }

}