package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.SurveyStatusEnum
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
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase
) : BaseViewModel() {
    private var surveyId: Int = 3
    private var sectionId: Int = 1
    private var subjectId: Int = 709
    private var taskId: Int = 5

    val isButtonEnable = mutableStateOf<Boolean>(false)
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
                    //   saveSurveyAnswerUseCase.saveSurveyAnswer(event,subjectId)

                }
            }

        }
    }

    fun intiQuestions() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _questionUiModel.value = fetchDataUseCase.invoke(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = subjectId
            )
            checkButtonValidation()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun saveAnswerIntoDB(
        question: QuestionUiModel
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            saveSurveyAnswerUseCase.saveSurveyAnswer(question, subjectId)
            taskStatusUseCase.markTaskInProgress(subjectId = subjectId, taskId)
            checkButtonValidation()
        }

    }


    private fun checkButtonValidation() {
        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->

            val result = (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
            if (!result) {
                isButtonEnable.value = false
                return
            }

        }
        isButtonEnable.value = true

    }

    fun saveButtonClicked() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskStatusUseCase.markTaskCompleted(subjectId = subjectId, taskId = taskId)
        }
    }

    fun isTaskStatusCompleted(): Boolean {
        return taskStatusUseCase.getTaskStatus(
            userId = saveSurveyAnswerUseCase.getUserIdentifier(),
            taskId = taskId,
            subjectId = subjectId
        )
            ?.equals(
                SurveyStatusEnum.COMPLETED.name
            ) ?: false
    }

}