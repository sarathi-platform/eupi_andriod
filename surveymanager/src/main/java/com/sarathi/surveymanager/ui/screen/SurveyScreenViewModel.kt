package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.domain.FetchDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase
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
            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val surveyAnswerEntity = SurveyAnswerEntity(
                        id = 0,
                        userId = event.userId,
                        questionId = event.questionId,
                        subjectId = event.subjectId,
                        surveyId = event.surveyId,
                        sectionId = event.sectionId,
                        referenceId = event.referenceId,
                        questionType = event.questionType,
                        taskId = event.taskId,
                        answerValue = event.answerValue,
                        optionItems = event.optionItems,
                        questionSummary = event.questionSummary,
                        needsToPost = event.needsToPost
                    )
                    saveSurveyAnswerUseCase.saveSurveyAnswer(surveyAnswerEntity)
                }
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

    fun saveAnswerIntoDB(
        question: QuestionUiModel,
        selectedValue: String
    ) {
        onEvent(
            EventWriterEvents.SaveAnswerEvent(
                userId = "",
                questionId = question.questionId,
                subjectId = 1,
                surveyId = question.surveyId,
                sectionId = question.sectionId,
                referenceId = 0,
                answerValue = selectedValue,
                questionType = question.type,
                taskId = 0,
                optionItems = listOf(),
                questionSummary = question.questionSummary,
                needsToPost = true
            )
        )
    }
}