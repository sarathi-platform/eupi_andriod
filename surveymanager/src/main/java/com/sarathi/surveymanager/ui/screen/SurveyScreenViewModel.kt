package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
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
class SurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase,
) : BaseViewModel() {
    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel
    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                intiQuestions()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun intiQuestions() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _questionUiModel.value = fetchDataUseCase.fetchSurveyDataFromDB.invoke()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }
}