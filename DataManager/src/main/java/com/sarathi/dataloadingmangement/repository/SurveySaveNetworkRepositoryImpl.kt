package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
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
    private val coreSharedPrefs: CoreSharedPrefs,
) : ISurveySaveNetworkRepository {
    override suspend fun getSurveyAnswerFromNetwork(surveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>> {
        return dataLoadingApiService.getSurveyAnswers(surveyAnswerRequest)
    }

    override suspend fun getSurveyIds(): List<Int> {
        return activityConfigDao.getSurveyIds()
    }

    override fun saveSurveyAnswerToDb(surveyApiResponse: List<QuestionAnswerResponseModel>) {

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
                    questionTag = questionEntity?.tag ?: DEFAULT_ID,
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    optionsUiModel = getOptionUiModels(
                        questionAnswerResponse.question?.questionId ?: DEFAULT_ID,
                        optionItems,
                        questionAnswerResponse.question?.options ?: listOf()
                    )

                )
            )
        }
    }

    private fun getOptionUiModels(
        questionId: Int,
        optionItems: List<OptionsUiModel>,
        optionsFromServer: List<QuestionOptionsResponseModel>
    ): List<OptionsUiModel> {
        val optionList = ArrayList<OptionsUiModel>()
        optionsFromServer.forEach { optionFromServer ->
            val selectedOption =
                optionItems.find { it.optionId == optionFromServer.optionId && it.questionId == questionId }
            selectedOption?.isSelected = true
            selectedOption?.selectedValue = optionFromServer.selectedValue
            if (selectedOption != null) {
                optionList.add(selectedOption)
            }
        }

        return optionList
    }

}
