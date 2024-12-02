package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
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
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    override fun checkButtonValidation(): Boolean {
        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->
            if (questionUiModel.tagId.contains(DISBURSED_AMOUNT_TAG)) {
                val disbursedAmount =
                    if (TextUtils.isEmpty(questionUiModel.options?.firstOrNull()?.selectedValue)) 0 else questionUiModel.options?.firstOrNull()?.selectedValue?.toInt()
                if (sanctionAmount != 0 && (disbursedAmount
                        ?: 0) + totalRemainingAmount > sanctionAmount
                ) {
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