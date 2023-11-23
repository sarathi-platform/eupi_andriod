package com.nrlm.baselinesurvey.ui.question_screen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val questionScreenUseCase: QuestionScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionDetail = mutableStateOf<SectionListItem>(SectionListItem(contentList = listOf(ContentList(BLANK_STRING, BLANK_STRING)), languageId = 2))
    val sectionDetail: State<SectionListItem> get() = _sectionDetail

    val totalQuestionCount = sectionDetail.value.questionList.size
    val answeredQuestionCount = mutableStateOf(sectionDetail.value.questionAnswerMapping.values.size)

    val showExpandedImage = mutableStateOf(false)

    val expandedImagePath = mutableStateOf("")

    fun init(sectionId: Int, surveyeeId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedlanguageId = questionScreenUseCase.getSectionUseCase.getSelectedLanguage()
            _sectionDetail.value = questionScreenUseCase.getSectionUseCase.invoke(sectionId, selectedlanguageId)
            val questionAnswerMap = mutableMapOf<Int, List<OptionsItem>>()
            val localAnswerList = questionScreenUseCase.getSectionAnswersUseCase.getSectionAnswerForDidi(sectionId = sectionId, didiId = surveyeeId)
            Log.d("TAG", "init: localAnswerList-> $localAnswerList")
            localAnswerList.forEach {
                if (!questionAnswerMap.containsKey(it.questionId)) {
                    questionAnswerMap.put(it.questionId, it.optionItems)
                }
            }
            Log.d("TAG", "init: questionAnswerMap-> $questionAnswerMap")
            _sectionDetail.value = _sectionDetail.value.copy(
                questionAnswerMapping = questionAnswerMap
            )
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
            is QuestionScreenEvents.SectionProgressUpdated -> {
                CoroutineScope(Dispatchers.IO).launch {
                    questionScreenUseCase.updateSectionProgressUseCase.invoke(event.surveyId, event.sectionId, event.didiId, event.sectionStatus)
                }
            }
            is QuestionScreenEvents.RatioTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = listOf(event.optionsItem),
                    questionEntity = event.questionEntity
                )
            }
            is QuestionScreenEvents.ListTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = listOf(event.optionsItem),
                    questionEntity = event.questionEntity
                )
            }
            is QuestionScreenEvents.GridTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = event.optionsItems,
                    questionEntity = event.questionEntity
                )
            }
            is QuestionScreenEvents.SendAnswersToServer -> {
//                questionScreenUseCase.
            }
        }
    }

    private fun saveOrUpdateSectionAnswers(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        optionsItem: List<OptionsItem>,
        questionEntity: QuestionEntity
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isQuestionAlreadyAnswered =
                    questionScreenUseCase.saveSectionAnswerUseCase.isQuestionAlreadyAnswered(
                        didiId = didiId,
                        sectionId = sectionId,
                        questionId = questionId
                    )
                if (isQuestionAlreadyAnswered > 0) {
                    questionScreenUseCase.saveSectionAnswerUseCase.updateSectionAnswerForDidi(
                        didiId = didiId,
                        questionId = questionId,
                        sectionId = sectionId,
                        optionItems = optionsItem,
                        questionType = questionEntity.type ?: BLANK_STRING,
                        questionSummary = questionEntity.questionSummary ?: BLANK_STRING
                    )
                } else {
                    questionScreenUseCase.saveSectionAnswerUseCase.saveSectionAnswerForDidi(
                        questionId = questionId,
                        didiId = didiId,
                        sectionId = sectionId,
                        surveyId = surveyId,
                        questionType = questionEntity.type ?: BLANK_STRING,
                        questionSummary = questionEntity.questionSummary ?: BLANK_STRING,
                        optionItems = optionsItem

                    )
                }
            } catch (ex: Exception) {
                BaselineLogger.e(
                    "QuestionScreenViewModel",
                    "saveOrUpdateSectionAnswers -> questionId = $questionId,\n" +
                            "                        didiId = $didiId,\n" +
                            "                        sectionId = $sectionId,\n" +
                            "                        surveyId = $surveyId,\n" +
                            "                        questionType = ${questionEntity.type ?: BLANK_STRING},\n" +
                            "                        questionSummary = ${questionEntity.questionSummary ?: BLANK_STRING},\n" +
                            "                        optionItems = $optionsItem ",
                    ex
                )
            }
        }
    }

}
