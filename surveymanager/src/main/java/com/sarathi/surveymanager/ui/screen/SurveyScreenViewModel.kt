package com.sarathi.surveymanager.ui.screen

import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyValidationsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveTransactionMoneyJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusEventWriterUserCase
import com.sarathi.dataloadingmangement.domain.use_case.SectionStatusUpdateUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val sectionStatusEventWriterUserCase: SectionStatusEventWriterUserCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fromEUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val saveTransactionMoneyJournalUseCase: SaveTransactionMoneyJournalUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val sectionStatusUpdateUseCase: SectionStatusUpdateUseCase,
    private val getSectionListUseCase: GetSectionListUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase,
    private val getSurveyValidationsFromDbUseCase: GetSurveyValidationsFromDbUseCase,
    private val validationUseCase: SurveyValidationUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase

) : BaseSurveyScreenViewModel(
    fetchDataUseCase,
    taskStatusUseCase,
    saveSurveyAnswerUseCase,
    surveyAnswerEventWriterUseCase,
    matStatusEventWriterUseCase,
    getTaskUseCase,
    getActivityUseCase,
    fromEUseCase,
    formEventWriterUseCase,
    coreSharedPrefs,
    getActivityUiConfigUseCase,
    getSectionListUseCase,
    getConditionQuestionMappingsUseCase,
    getSurveyConfigFromDbUseCase,
    getSurveyValidationsFromDbUseCase,
    validationUseCase,
    fetchContentUseCase = fetchContentUseCase,
    fetchAppConfigFromCacheOrDbUsecase = fetchAppConfigFromCacheOrDbUsecase
) {

    override fun saveSingleAnswerIntoDb(currentQuestionUiModel: QuestionUiModel) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            saveQuestionAnswerIntoDb(currentQuestionUiModel)

            surveyAnswerEventWriterUseCase.saveSurveyAnswerEvent(
                questionUiModel = currentQuestionUiModel,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = granType,
                taskId = taskId,
                uriList = ArrayList(),
                isFromRegenerate = false,
                activityId = activityConfig?.activityId.value(),
                activityReferenceId = activityConfig?.referenceId,
                activityReferenceType = activityConfig?.referenceType
            )
        }
    }

    fun updateSectionStatus(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String,
        callBack: () -> Unit
    ) {
        ioViewModelScope {
            sectionStatusUpdateUseCase.invoke(
                missionId = missionId,
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId, status = status
            )
            sectionStatusEventWriterUserCase(
                surveyId, sectionId, taskId, status, isFromRegenerate = false
            )
            withContext(mainDispatcher) {
                callBack()
            }
        }
    }

    override suspend fun intiQuestions() {
        super.intiQuestions()

        questionUiModel.value.filterForValidations(visibilityMap).apply {

            //If the filtered list is empty run button check to enable or disable submit button.
            if (this.isEmpty()) {
                isButtonEnable.value = isButtonEnabled(true)
                return@apply
            }

            this.forEach {
                runValidationCheck(it.questionId) { isValid, message ->
                    try {
                        fieldValidationAndMessageMap[it.questionId] =
                            Pair(isValid, message)
                    } catch (ex: Exception) {
                        CoreLogger.e(
                            tag = LOGGER_TAG,
                            msg = "Exception: intiQuestions -> runValidationCheck@lambda: ${ex.message}",
                            ex = ex
                        )
                    }
                }
            }

        }
    }
}
