package com.sarathi.surveymanager.ui.screen

import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.OUTFLOW
import com.sarathi.dataloadingmangement.domain.use_case.FetchInfoUiModelUseCase
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
import com.sarathi.dataloadingmangement.domain.use_case.MoneyJournalForPopEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveTransactionMoneyJournalForPopUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.MissionFilterUtils
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LivelihoodPopSurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fromEUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getSectionListUseCase: GetSectionListUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase,
    private val getSurveyValidationsFromDbUseCase: GetSurveyValidationsFromDbUseCase,
    private val validationUseCase: SurveyValidationUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase,
    private val saveTransactionMoneyJournalForPopUseCase: SaveTransactionMoneyJournalForPopUseCase,
    private val moneyJournalForPopEventWriterUseCase: MoneyJournalForPopEventWriterUseCase,
    private val fetchInfoUiModelUseCase: FetchInfoUiModelUseCase,
    private val missionFilterUtils: MissionFilterUtils
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
    fetchAppConfigFromCacheOrDbUsecase = fetchAppConfigFromCacheOrDbUsecase,
    fetchInfoUiModelUseCase = fetchInfoUiModelUseCase,
    missionFilterUtils = missionFilterUtils
) {

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
                            Triple(
                                isValid,
                                message,
                                if (QuestionType.userInputQuestionTypeList.contains(
                                        it.type.toLowerCase()
                                    )
                                ) (it.options?.firstOrNull()?.selectedValue
                                    ?: com.nudge.core.BLANK_STRING) else null
                            )
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

    override fun saveSingleAnswerIntoDb(question: QuestionUiModel) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            saveQuestionAnswerIntoDb(question)

            surveyAnswerEventWriterUseCase.saveSurveyAnswerEvent(
                questionUiModel = question,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = granType,
                taskId = taskId,
                uriList = ArrayList(),
                activityId = activityConfig?.activityId.value(),
                activityReferenceId = activityConfig?.referenceId,
                activityReferenceType = activityConfig?.referenceType,
                isFromRegenerate = false
            )
        }
    }


    fun checkAndWriteMoneyJournalEvent() {
        ioViewModelScope {
            val config = activityConfig?.moneyJournalConfig ?: return@ioViewModelScope
            val tags = config.tags ?: return@ioViewModelScope
            if (tags.isEmpty()) return@ioViewModelScope

            val filteredQuestions = questionUiModel.value
                .filter {
                    it.tagId.any { tag -> tags.contains(tag) }
                }

            if (!conditionsUtils.areAllQuestionsVisible(filteredQuestions)) return@ioViewModelScope

            val moneyJournalEntity =
                saveTransactionMoneyJournalForPopUseCase.saveMoneyJournalForSurvey(
                    subjectId = taskEntity?.subjectId.value(),
                    subjectType = subjectType,
                    transactionId = UUID.randomUUID().toString(),
                    referenceId = activityConfig?.referenceId.value(),
                    referenceType = activityConfig?.referenceType.value(),
                    questionUiModels = filteredQuestions,
                    transactionFlow = OUTFLOW
                )

            moneyJournalForPopEventWriterUseCase.writeMoneyJournalEventForPop(
                moneyJournalEntity,
                activityConfig?.referenceType.value()
            )
        }
    }

}