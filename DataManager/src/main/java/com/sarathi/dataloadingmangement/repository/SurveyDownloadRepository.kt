package com.sarathi.dataloadingmangement.repository

import android.util.Log
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.data.entities.SectionEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.model.survey.response.ConditionDtoWithParentId
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.QuestionList
import com.sarathi.dataloadingmangement.model.survey.response.Sections
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class SurveyDownloadRepository @Inject constructor(
    val dataLoadingApiService: DataLoadingApiService,
    val surveyDao: SurveyEntityDao,
    val sectionEntityDao: SectionEntityDao,
    val coreSharedPrefs: CoreSharedPrefs,
    val optionItemDao: OptionItemDao,
    val questionEntityDao: QuestionEntityDao
) : ISurveyDownloadRepository {
    override suspend fun fetchSurveyFromNetwork(surveyRequest: SurveyRequest): ApiResponseModel<SurveyResponseModel> {
        return dataLoadingApiService.getSurveyFromNetwork(surveyRequest)
    }

    override fun saveSurveyToDb(surveyApiResponse: SurveyResponseModel) {
        surveyDao.deleteSurveyFroLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyApiResponse.surveyId
        )
        surveyApiResponse.surveyLanguageAttributes.forEach { surveyLanguageAttributes ->
            surveyDao.insertSurvey(
                SurveyEntity.getSurveyEntity(
                    coreSharedPrefs.getUniqueUserIdentifier(),
                    surveyLanguageAttributes.surveyName,
                    surveyLanguageAttributes.languageCode,
                    surveyApiResponseModel = surveyApiResponse
                )
            )

            surveyLanguageAttributes.sections.forEach { section ->

                val contentLists = mutableListOf<ContentList>()
                sectionEntityDao.deleteSurveySectionFroLanguage(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    section.sectionId,
                    surveyApiResponse.surveyId,
                    surveyLanguageAttributes.languageCode
                )
                contentLists.addAll(section.contentList)
                sectionEntityDao.insertSection(
                    SectionEntity.getSectionEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        section = section,
                        surveyLanguageAttributes.languageCode,
                        surveyApiResponse.surveyId
                    )
                )


                val conditionDtoWithParentIdList =
                    mutableListOf<ConditionDtoWithParentId>()

                section.questionList.forEach { question ->
                    conditionDtoWithParentIdList.add(ConditionDtoWithParentId(question!!, 0))
                }
                saveQuestionOptionsAtAllLevel(
                    conditionDtoWithParentIdList,
                    section,
                    surveyApiResponse,
                    section.languageCode
                )
            }
        }

    }

    private fun saveQuestionOptionsAtAllLevel(
        questionList: List<ConditionDtoWithParentId?>,
        section: Sections,
        surveyResponseModel: SurveyResponseModel,
        languageId: String,
        isSubQuestionList: Boolean = false
    ) {
        questionList.forEach { question ->

            saveQuestionAndOptionsToDb(
                question = question?.resultList,
                section = section,
                surveyResponseModel = surveyResponseModel,
                languageId = languageId,
                isSubQuestionList = isSubQuestionList
            )
            question?.resultList?.options?.forEach { opt ->
                opt?.conditions?.forEach { conditionDto ->
                    conditionDto?.let { conditionsDto ->
                        if (conditionsDto.resultType == ResultType.Questions.name) {
                            val conditionDtoWithParentIdList =
                                mutableListOf<ConditionDtoWithParentId>()
                            conditionsDto.resultList.forEach {
                                val conditionDtoWithParentId = ConditionDtoWithParentId(
                                    it, question.resultList.questionId ?: 0
                                )
                                conditionDtoWithParentIdList.add(conditionDtoWithParentId)
                            }

                            saveQuestionOptionsAtAllLevel(
                                questionList = conditionDtoWithParentIdList,
                                section = section,
                                surveyResponseModel = surveyResponseModel,
                                languageId = languageId,
                                isSubQuestionList = true
                            )
                        }
                    }
                }
            }
        }
    }


    private fun saveQuestionAndOptionsToDb(
        question: QuestionList?,
        section: Sections,
        surveyResponseModel: SurveyResponseModel,
        languageId: String,
        isSubQuestionList: Boolean = false,
        parentId: Int = 0
    ) {
        try {
            if (question?.questionId != null) {
                val existingQuestion = questionEntityDao.getQuestionForSurveySectionForLanguage(
                    userid = coreSharedPrefs.getUniqueUserIdentifier(),
                    question.questionId!!,
                    section.sectionId,
                    surveyResponseModel.surveyId,
                    languageId
                )
                if (existingQuestion != null) {
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(
                        userid = coreSharedPrefs.getUniqueUserIdentifier(),
                        question.questionId!!,
                        section.sectionId,
                        surveyResponseModel.surveyId,
                        languageId
                    )
                }
                Log.d("TAG", "saveQuestionAndOptionsToDb: question -> ${question.questionDisplay}")

                questionEntityDao.insertQuestion(
                    QuestionEntity.getQuestionEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        languageId = languageId,
                        isCondition = isSubQuestionList,
                        question = question,
                        parentId = parentId,
                        surveyId = surveyResponseModel.surveyId,
                        sectionId = section.sectionId
                    )
                )
                question.options?.forEach { optionsItem ->
                    if (optionsItem != null) {
                        optionItemDao.deleteSurveySectionQuestionOptionFroLanguage(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            optionsItem.optionId!!,
                            question.questionId!!,
                            section.sectionId,
                            surveyResponseModel.surveyId,
                            languageId
                        )
                        optionItemDao.insertOption(
                            OptionItemEntity.getOptionItemEntity(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                optionsItem = optionsItem,
                                sectionId = section.sectionId,
                                surveyId = surveyResponseModel.surveyId,
                                questionId = question.questionId,
                                languageId = languageId
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(
                "DataLoadingScreenRepositoryImpl",
                "saveQuestionAndOptionsToDb: exception, question: $question",
                ex
            )
        }

    }


}

enum class ResultType {
    Options,
    Questions,
    Formula,
    NoneMarked
}