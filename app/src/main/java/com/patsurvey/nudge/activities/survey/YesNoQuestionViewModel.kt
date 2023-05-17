package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.model.dataModel.YesNoAnswerModel
import com.patsurvey.nudge.model.dataModel.YesNoQuestionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YesNoQuestionViewModel @Inject constructor(
    val prefRepo: PrefRepo
) : BaseViewModel() {
    var questionList = mutableStateListOf(
        YesNoQuestionModel(1, "Is anyone in the family in government service?"),
        YesNoQuestionModel(2, "Is anyone have any vehicle in your family?"),
        YesNoQuestionModel(3, "Is anyone in the family in business?"),
        YesNoQuestionModel(4, "Is anyone in the family in farming?"),
        YesNoQuestionModel(5, "Is anyone in the family in agriculture?"),
        YesNoQuestionModel(6, "Is anyone in the family in school?")
    )
        private set

    var answerList = mutableStateListOf<YesNoAnswerModel>()
        private set

    var currentIndex by mutableStateOf(0)

    private val _questionAnswerUiState = MutableStateFlow(QuestionAnswerUiState(questionList[0].question, currentIndex+1, answer = false, answered = false))
    val questionAnswerUiState: StateFlow<QuestionAnswerUiState> = _questionAnswerUiState.asStateFlow()

    private val surveyHeader = YesNoQuestionViewModel.SurveyHeaderUiState(
        didiDetailsModel = DidiDetailsModel(
            1,
            "Urmila Devi",
            "Sundar Pahar",
            "Sundar Pahar",
            "Kahar",
            "112",
            "Rajesh"
        ),
        "PAT Survey",
        questionCount = questionList.size,
        answeredCount = answerList.size,
        partNumber = 1
    )
    private val _surveyHeaderUiState = MutableStateFlow(surveyHeader)
    val surveyHeaderUiState: StateFlow<SurveyHeaderUiState> = _surveyHeaderUiState.asStateFlow()

    private val _nextPreviousUiState = MutableStateFlow(NextPreviousUiState())
    val nextPreviousUiState: StateFlow<NextPreviousUiState> = _nextPreviousUiState.asStateFlow()


    fun addAnswer(questionModel: YesNoQuestionModel, answer: Boolean) {
        answerList.add(
            YesNoAnswerModel(
                questionModel.id,
                questionModel.question,
                answer = answer,
                questionAnswered = true
            )
        )
    }

    fun OnEvent(event: MainEvent) {
        Log.i("OnEvent", "called event is $event")
        viewModelScope.launch(Dispatchers.Main) {
            when(event) {
                is MainEvent.OnButtonClicked -> {
                    if(answerList.size>currentIndex){
                        val updateAnswer = answerList[currentIndex].copy(answer = event.isYes)
                        answerList.removeAt(currentIndex)
                        answerList.add(currentIndex, updateAnswer)
                    } else {
                        addAnswer(questionList[currentIndex], event.isYes)
                    }
                    _questionAnswerUiState.value = questionAnswerUiState.value.copy(answer = event.isYes, answered = true)
                    delay(500)
                    if(currentIndex < questionList.size-1){
                        currentIndex += 1
                        val questionModel = questionList[currentIndex]
                        _questionAnswerUiState.value = QuestionAnswerUiState(
                            questionModel.question, currentIndex + 1, answer = false, answered = false
                        )
                    }
                    updateUi()
                }
                MainEvent.OnPreviousClicked -> {
                    currentIndex -= 1
                    updateUi()
                }
                MainEvent.OnNextClicked -> {
                    currentIndex += 1
                    updateUi()
                }
            }
        }
    }

    private fun updateUi() {
        if (currentIndex < answerList.size) {
            val answerModel = answerList[currentIndex]
            _questionAnswerUiState.value = QuestionAnswerUiState(
                answerModel.question,
                currentIndex + 1,
                answer = answerModel.answer,
                answered = answerModel.questionAnswered
            )
        } else if(currentIndex < questionList.size) {
            val questionModel = questionList[currentIndex]
            _questionAnswerUiState.value = QuestionAnswerUiState(
                questionModel.question,
                currentIndex + 1,
                answer = false,
                answered = false
            )
        }
        _surveyHeaderUiState.value = surveyHeaderUiState.value.copy(answeredCount = answerList.size)
        Log.i("YesNoViewModel", "current Index is $currentIndex, answerListSize is ${answerList.size}")
        val nextText = if(currentIndex < answerList.size && currentIndex+2 <= questionList.size) {
            /*currentindex+1 will be showing question Number, and currentIndex + 1 + 1 will be be show next question*/
            "Q${currentIndex+2}"
        } else {
            null
        }
        val previousText = if(questionList.size>0 && currentIndex>0){
            "Q${currentIndex}"
        } else {
            null
        }

        Log.i("YesNoViewModel", "current Index previousText$previousText, nextText is ${nextText}")
        _nextPreviousUiState.value = NextPreviousUiState(
            nextText != null,
            previousText != null,
            nextText = nextText ?: "",
            previousText = previousText ?: ""
        )
    }

    sealed class MainEvent {
        data class OnButtonClicked(val isYes : Boolean): MainEvent()
        object OnNextClicked: MainEvent()
        object OnPreviousClicked: MainEvent()
    }

    data class QuestionAnswerUiState(
        val question: String = "",
        val questionNumber: Int = 1,
        val answer: Boolean = false,
        val answered: Boolean = false,
    )

    data class SurveyHeaderUiState(
        val didiDetailsModel: DidiDetailsModel,
        val surveyTitle: String,
        val questionCount: Int,
        val answeredCount: Int,
        val partNumber : Int
    )

    data class NextPreviousUiState(
        val nextVisible: Boolean = false,
        val previousVisible: Boolean = false,
        val nextText: String = "",
        val previousText: String = ""
    )




}