package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
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
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrantSurveyScreenViewModel @Inject constructor(
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
    private val saveTransactionMoneyJournalUseCase: SaveTransactionMoneyJournalUseCase,
    private val getSectionListUseCase: GetSectionListUseCase,
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


    fun saveButtonClicked() {
        saveAllAnswerIntoDB()

    }

    fun saveAllAnswerIntoDB() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            questionUiModel.value.forEach { question ->
                saveQuestionAnswerIntoDb(question)
            }
            if (sanctionAmount != 0) {
                val formEntity = fromEUseCase.saveFormEData(
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    taskId = taskId,
                    surveyId = surveyId,
                    missionId = taskEntity?.missionId ?: -1,
                    activityId = taskEntity?.activityId ?: -1,
                    subjectType = subjectType,
                    referenceId = referenceId
                )
                formEventWriterUseCase.writeFormEvent(
                    surveyName = questionUiModel.value.firstOrNull()?.surveyName
                        ?: com.sarathi.dataloadingmangement.BLANK_STRING,
                    formEntity = formEntity,

                    )
            }
            if (taskEntity?.status == SurveyStatusEnum.NOT_STARTED.name || taskEntity?.status == SurveyStatusEnum.NOT_AVAILABLE.name || taskEntity?.status == SurveyStatusEnum.COMPLETED.name) {
                taskStatusUseCase.markTaskInProgress(
                    taskId = taskId
                )
                taskStatusUseCase.markActivityInProgress(
                    missionId = taskEntity?.missionId ?: DEFAULT_ID,
                    activityId = taskEntity?.activityId ?: DEFAULT_ID,
                )
                taskStatusUseCase.markMissionInProgress(
                    missionId = taskEntity?.missionId ?: DEFAULT_ID,
                )
                taskEntity = getTaskUseCase.getTask(taskId)
                taskEntity?.let {
                    matStatusEventWriterUseCase.markMATStatus(
                        surveyName = questionUiModel.value.firstOrNull()?.surveyName
                            ?: com.sarathi.dataloadingmangement.BLANK_STRING,
                        subjectType = subjectType,
                        missionId = taskEntity?.missionId ?: DEFAULT_ID,
                        activityId = taskEntity?.activityId ?: DEFAULT_ID,
                        taskId = taskEntity?.taskId ?: DEFAULT_ID,
                        isFromRegenerate = false

                    )
                }

            }
            saveTransactionMoneyJournalUseCase.saveMoneyJournalForGrant(
                referenceId = referenceId,
                grantId = grantID,
                grantType = granType,
                questionUiModels = questionUiModel.value,
                subjectId = taskEntity?.subjectId ?: -1,
                subjectType = subjectType
            )
            surveyAnswerEventWriterUseCase.invoke(
                questionUiModels = questionUiModel.value,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId
                    ?: com.sarathi.dataloadingmangement.BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = granType,
                taskId = taskId,
                isFromRegenerate = false,
                activityId = activityConfig?.activityId.value(),
                activityReferenceId = activityConfig?.referenceId,
                activityReferenceType = activityConfig?.referenceType
            )


        }


    }

    override fun checkButtonValidation(): Boolean {
        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->
            if (questionUiModel.tagId.contains(DISBURSED_AMOUNT_TAG)) {
                val disbursedAmount =
                    if (TextUtils.isEmpty(questionUiModel.options?.firstOrNull()?.selectedValue)) 0 else questionUiModel.options?.firstOrNull()?.selectedValue?.toInt()
                val disbursedAmt = disbursedAmount ?: 0
                if (sanctionAmount != 0 && disbursedAmt + totalRemainingAmount > sanctionAmount) {
                    return false
                }
            }
            val result = (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
            if (!result) {
                return false
            }
        }
        return true
    }


}