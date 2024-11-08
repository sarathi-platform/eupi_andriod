package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.nudge.core.utils.CoreLogger
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchPatQuestionRepository
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.model.request.GetQuestionListRequest
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.HEADING_QUESTION_TYPE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.SUCCESS
import javax.inject.Inject

class FetchPatQuestionUseCase @Inject constructor(
    private val fetchPatQuestionRepository: FetchPatQuestionRepository,
    private val languageListUseCase: LanguageListUseCase
) {

    private val LOGGING_TAG = FetchPatQuestionUseCase::class.java.simpleName

    suspend operator fun invoke(isRefresh: Boolean) {
        val stateId = fetchPatQuestionRepository.getStateId()
        languageListUseCase.invoke().map { it.id }.distinct().apply {
            this.forEach { languageId ->

                try {
                    val localQuestionList =
                        fetchPatQuestionRepository.getAllQuestionsForLanguage(languageId)
                    if (localQuestionList.isEmpty() || isRefresh) {
                        val questionResponse =
                            fetchPatQuestionRepository.fetchQuestionListFromNetwork(
                                GetQuestionListRequest(
                                    languageId = languageId,
                                    stateId = stateId,
                                    surveyName = if (fetchPatQuestionRepository.isBpcUser()) BPC_SURVEY_CONSTANT else PAT_SURVEY_CONSTANT
                                )
                            )

                        if (questionResponse.status.equals(SUCCESS, true)) {

                            questionResponse.data?.let { questionList ->

                                if (isRefresh) {
                                    fetchPatQuestionRepository.deleteQuestionTableForLanguage(
                                        languageId
                                    )
                                }

                                questionList.listOfQuestionSectionList?.forEach { list ->

                                    list?.questionList?.forEach { question ->
                                        question?.sectionOrderNumber = list.orderNumber
                                        question?.actionType = list.actionType
                                        question?.languageId = languageId
                                        question?.surveyId = questionList.surveyId
                                        question?.thresholdScore =
                                            questionList.thresholdScore
                                        question?.surveyPassingMark =
                                            questionList.surveyPassingMark
                                        NudgeLogger.d(
                                            "TAG",
                                            "fetchQuestionsList: ${question?.options.toString()}"
                                        )
                                        if (question?.questionFlag.equals(QUESTION_FLAG_WEIGHT) || question?.questionFlag.equals(
                                                QUESTION_FLAG_RATIO
                                            )
                                        ) {
                                            val heading = question?.options?.filter {
                                                it.optionType.equals(
                                                    HEADING_QUESTION_TYPE, true
                                                )
                                            }?.get(0)?.display
                                            question?.headingProductAssetValue = heading
                                        }

                                    }
                                    list?.questionList?.let {
                                        fetchPatQuestionRepository.saveQuestionsToDb(it as List<QuestionEntity>)
                                    }

                                }

                            }

                        }

                    }
                } catch (e: Exception) {
                    CoreLogger.e(
                        tag = LOGGING_TAG,
                        msg = "invoke: Exception-> ${e.message}",
                        ex = e,
                        stackTrace = true
                    )
                }
            }
        }
    }

}