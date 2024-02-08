package com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.FormQuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QuestionTypeScreenViewModel @Inject constructor(
    private val formQuestionScreenUseCase: FormQuestionScreenUseCase
) : BaseViewModel() {

    private val _loaderState = mutableStateOf(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    val optionList: State<List<OptionItemEntity>> get() = _optionList
    private val _optionList = mutableStateOf<List<OptionItemEntity>>(emptyList())

    val referenceId: String = UUID.randomUUID().toString()

    fun init(sectionId: Int, surveyId: Int, questionId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _optionList.value =
                formQuestionScreenUseCase.getFormQuestionResponseUseCase.invoke(
                    surveyId,
                    sectionId,
                    questionId
                )
            Log.d("TAG", "init: questionOptionList-> ${optionList.value}")
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is QuestionTypeEvent.SaveFormQuestionResponseEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val formQuestionResponseForQuestionOption = formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestionOption(
                        event.formQuestionResponseEntity.surveyId,
                        event.formQuestionResponseEntity.sectionId,
                        event.formQuestionResponseEntity.questionId,
                        event.formQuestionResponseEntity.referenceId,
                        event.formQuestionResponseEntity.didiId
                    )
                    if (formQuestionResponseForQuestionOption.any { it.optionId == event.formQuestionResponseEntity.optionId }) {
                        formQuestionScreenUseCase.updateFormQuestionResponseUseCase.invoke(
                            event.formQuestionResponseEntity.surveyId,
                            event.formQuestionResponseEntity.sectionId,
                            event.formQuestionResponseEntity.questionId,
                            event.formQuestionResponseEntity.optionId,
                            event.formQuestionResponseEntity.selectedValue,
                            event.formQuestionResponseEntity.referenceId,
                            event.formQuestionResponseEntity.didiId
                        )
                    } else {
                        formQuestionScreenUseCase.saveFormQuestionResponseUseCase.invoke(event.formQuestionResponseEntity)
                    }
                }

            }
        }
    }
}
