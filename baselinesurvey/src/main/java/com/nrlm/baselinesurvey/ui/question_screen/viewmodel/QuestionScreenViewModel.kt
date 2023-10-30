package com.nrlm.baselinesurvey.ui.question_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.Sections
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val questionScreenUseCase: QuestionScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionDetail = mutableStateOf<Sections>(Sections())
    val sectionDetail: State<Sections> get() = _sectionDetail

    fun init(sectionId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        _sectionDetail.value = questionScreenUseCase.getSectionUseCase.invoke(sectionId)
        onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }    }

}
