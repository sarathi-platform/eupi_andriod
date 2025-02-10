package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.model.survey.response.OptionsItem
import com.sarathi.dataloadingmangement.model.survey.response.QuestionAnswerResponseModel
import com.sarathi.dataloadingmangement.model.survey.response.QuestionOptionsResponseModel
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject


class SurveySaveNetworkRepositoryImpl @Inject constructor(
    private val surveyAnswersDao: SurveyAnswersDao,
    private val activityConfigDao: ActivityConfigDao,
    private val questionEntityDao: QuestionEntityDao,
    private val optionItemDao: OptionItemDao,
    private val dataLoadingApiService: DataLoadingApiService,
    private val grantConfigDao: GrantConfigDao,
    private val taskDao: TaskDao,
    private val coreSharedPrefs: CoreSharedPrefs,
) : ISurveySaveNetworkRepository {
    override suspend fun getSurveyAnswerFromNetwork(surveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>> {
        return dataLoadingApiService.getSurveyAnswers(surveyAnswerRequest)
    }

    override suspend fun getActivityConfig(missionId: Int): List<ActivityConfigEntity>? {
        return activityConfigDao.getActivityConfigUiModel(
            missionId = missionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


    override fun saveSurveyAnswerToDb(surveyApiResponse: List<QuestionAnswerResponseModel>) {
        try {
            surveyApiResponse.forEach { questionAnswerResponse ->
                val optionItems = optionItemDao.getSurveySectionQuestionOptionsForLanguage(
                    languageId = coreSharedPrefs.getAppLanguage(),
                    sectionId = questionAnswerResponse.sectionId.toInt(),
                    surveyId = questionAnswerResponse.surveyId,
                    referenceType = LanguageAttributeReferenceType.OPTION.name,
                    userId = coreSharedPrefs.getUniqueUserIdentifier()
                )


                val questionEntity = questionEntityDao.getQuestionEntity(
                    userid = coreSharedPrefs.getUniqueUserIdentifier(),
                    questionId = questionAnswerResponse.question?.questionId ?: DEFAULT_ID,
                    surveyId = questionAnswerResponse.surveyId,
                    sectionId = questionAnswerResponse.sectionId.toInt()
                )

                surveyAnswersDao.insertSurveyAnswer(
                    SurveyAnswerEntity.getSurveyAnswerEntityFromQuestionAnswerResponse(
                        questionAnswerResponse = questionAnswerResponse,
                        questionSummary = questionEntity?.originalValue ?: BLANK_STRING,
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        optionsUiModel = getOptionUiModels(
                            questionAnswerResponse.question?.questionId ?: DEFAULT_ID,
                            optionItems,
                            questionAnswerResponse.question?.options ?: listOf(),
                            questionAnswerResponse.question?.tag ?: listOf(),
                            surveyId = questionAnswerResponse.surveyId,
                            sectionId = questionAnswerResponse.sectionId.toInt(),
                            grantId = questionAnswerResponse.grantId ?: 0,
                            taskId = questionAnswerResponse.taskId
                        )

                    )
                )
            }

        } catch (exception: Exception) {
            CoreLogger.e(tag = "SurveyAnswerException", msg = exception.stackTraceToString())
        }
    }

    private fun getOptionUiModels(
        questionId: Int,
        optionItems: List<OptionsUiModel>,
        optionsFromServer: List<QuestionOptionsResponseModel>,
        tag: List<Int>,
        surveyId: Int,
        sectionId: Int,
        grantId: Int,
        taskId: Int
    ): List<OptionsUiModel> {
        val optionList = ArrayList<OptionsUiModel>()
        val mergedOptionItem = ArrayList<OptionsUiModel>()
        mergedOptionItem.addAll(optionItems)
        if (tag.contains(MODE_TAG) || tag.contains(NATURE_TAG)) {
            getOptionsForModeAndNature(
                tag,
                mergedOptionItem,
                sectionId,
                surveyId,
                questionId,
                grantId = grantId,
                taskId = taskId
            )
        }
        optionsFromServer.forEach { optionFromServer ->
            val selectedOption =
                mergedOptionItem.find { it.optionId == optionFromServer.optionId && it.questionId == questionId }
            selectedOption?.isSelected = true
            selectedOption?.selectedValue = optionFromServer.selectedValue
            if (selectedOption != null) {
                optionList.add(selectedOption)
            }
        }

        return optionList
    }

    private fun getOptionsForModeAndNature(
        tag: List<Int>,
        mergedOptionItem: ArrayList<OptionsUiModel>,
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        grantId: Int,
        taskId: Int
    ) {
        val grantConfig = grantConfigDao.getGrantConfigWithGrantId(
            activityConfigId = getActivityConfigId(taskId),
            grantId = grantId
        )
        val type =
            object : TypeToken<List<OptionsItem?>?>() {}.type
        val options = Gson().fromJson<List<OptionsItem>>(
            if (tag.contains(MODE_TAG)) grantConfig?.grantMode else grantConfig?.grantNature,
            type

        )
        options?.forEach { option ->
            mergedOptionItem.add(
                OptionsUiModel(
                    sectionId = sectionId,
                    surveyId = surveyId,
                    questionId = questionId,
                    optionId = option?.optionId,
                    optionType = option?.optionType,
                    originalValue = option?.originalValue,
                    isSelected = false,
                    description = option?.surveyLanguageAttributes?.find { it.languageCode == coreSharedPrefs.getAppLanguage() }?.description,
                    paraphrase = option?.surveyLanguageAttributes?.find { it.languageCode == coreSharedPrefs.getAppLanguage() }?.paraphrase,
                    contentEntities = option?.contentList ?: listOf(),
                    conditions = option?.conditions
                )
            )

        }
    }

    private fun getActivityConfigId(taskId: Int): Int {
        try {
            val activityId = taskDao.getTaskById(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                taskId = taskId
            ).activityId
            return activityConfigDao.getActivityConfigWithSection(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                activityId = activityId ?: 0
            ).activityConfigId
        } catch (e: Exception) {
            CoreLogger.e(
                tag = "SurveySaveNetworkRepositoryImpl",
                msg = "getActivityConfigId: taskId -> $taskId, exception -> ${e.message}",
                ex = e,
                stackTrace = true
            )
            return 0
        }
    }

}
