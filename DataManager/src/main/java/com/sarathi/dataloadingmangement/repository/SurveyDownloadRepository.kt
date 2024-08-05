package com.sarathi.dataloadingmangement.repository

import android.util.Log
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyLanguageAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TagReferenceEntityDao
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.data.entities.SectionEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyLanguageAttributeEntity
import com.sarathi.dataloadingmangement.data.entities.TagReferenceEntity
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.model.survey.response.ConditionDtoWithParentId
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.QuestionList
import com.sarathi.dataloadingmangement.model.survey.response.Sections
import com.sarathi.dataloadingmangement.model.survey.response.SurveyLanguageAttributes
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class SurveyDownloadRepository @Inject constructor(
    val dataLoadingApiService: DataLoadingApiService,
    val surveyDao: SurveyEntityDao,
    val sectionEntityDao: SectionEntityDao,
    val coreSharedPrefs: CoreSharedPrefs,
    val optionItemDao: OptionItemDao,
    val questionEntityDao: QuestionEntityDao,
    val tagReferenceEntityDao: TagReferenceEntityDao,
    val surveyLanguageAttributeDao: SurveyLanguageAttributeDao
) : ISurveyDownloadRepository {
    override suspend fun fetchSurveyFromNetwork(surveyRequest: SurveyRequest): ApiResponseModel<SurveyResponseModel> {
        return dataLoadingApiService.getSurveyFromNetwork(surveyRequest)
    }

    override fun saveSurveyToDb(surveyApiResponse: SurveyResponseModel) {
        try {


            surveyDao.deleteSurvey(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                surveyApiResponse.surveyId
            )
            surveyDao.insertSurvey(
                SurveyEntity.getSurveyEntity(
                    coreSharedPrefs.getUniqueUserIdentifier(),
                    surveyApiResponseModel = surveyApiResponse
                )
            )

            deleteSurveyLanguageAttribute(
                surveyApiResponse.surveyId,
                LanguageAttributeReferenceType.SURVEY.name
            )

            saveSurveyLanguageAttributes(
                surveyApiResponse.surveyLanguageAttributes,
                surveyApiResponse.surveyId,
                LanguageAttributeReferenceType.SURVEY.name
            )


            surveyApiResponse.sections?.forEach { section ->
                deleteSurveyLanguageAttribute(
                    section.sectionId,
                    LanguageAttributeReferenceType.SECTION.name
                )

                saveSurveyLanguageAttributes(
                    section.surveyLanguageAttributes,
                    section.sectionId,
                    LanguageAttributeReferenceType.SECTION.name
                )
                tagReferenceEntityDao.deleteTagReferenceEntityForTag(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    referenceId = section.sectionId,
                    referenceType = LanguageAttributeReferenceType.SECTION.name
                )
                tagReferenceEntityDao.addTagReferenceEntity(
                    TagReferenceEntity.getTagReferenceEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        referenceType = LanguageAttributeReferenceType.SECTION.name,
                        tags = section.tag ?: listOf(),
                        referenceId = section.sectionId
                    )
                )

                val contentLists = mutableListOf<ContentList>()
                sectionEntityDao.deleteSurveySectionFroLanguage(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    section.sectionId,
                    surveyApiResponse.surveyId,
                )
                contentLists.addAll(section.contentList ?: listOf())
                sectionEntityDao.insertSection(
                    SectionEntity.getSectionEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        section = section,
                        surveyApiResponse.surveyId
                    )
                )


                val conditionDtoWithParentIdList =
                    mutableListOf<ConditionDtoWithParentId>()

                section.questionList?.forEach { question ->

                    conditionDtoWithParentIdList.add(ConditionDtoWithParentId(question!!, 0))
                }
                saveQuestionOptionsAtAllLevel(
                    conditionDtoWithParentIdList,
                    section,
                    surveyApiResponse,
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("Exceptiom", ex.localizedMessage)
        }

    }

    private fun saveSurveyLanguageAttributes(
        surveyLanguageAttributes: List<SurveyLanguageAttributes>?,
        referenceId: Int,
        referenceType: String
    ) {
        surveyLanguageAttributes?.forEach {
            surveyLanguageAttributeDao.insertSurveyLanguageAttribute(
                SurveyLanguageAttributeEntity.getSurveyLanguageAttributeEntity(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    languageAttributes = it,
                    referenceId = referenceId,
                    type = referenceType
                )
            )
        }
    }

    private fun deleteSurveyLanguageAttribute(referenceId: Int, referenceType: String) {
        surveyLanguageAttributeDao.deleteSurveyLanguageAttribute(referenceId, referenceType)
    }


    private fun saveQuestionOptionsAtAllLevel(
        questionList: List<ConditionDtoWithParentId?>,
        section: Sections,
        surveyResponseModel: SurveyResponseModel,
        isSubQuestionList: Boolean = false
    ) {
        questionList.forEach { question ->

            saveQuestionAndOptionsToDb(
                question = question?.resultList,
                section = section,
                surveyResponseModel = surveyResponseModel,
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
                )
                if (existingQuestion != null) {
                    questionEntityDao.deleteSurveySectionQuestionFroLanguage(
                        userid = coreSharedPrefs.getUniqueUserIdentifier(),
                        question.questionId!!,
                        section.sectionId,
                        surveyResponseModel.surveyId,
                    )
                }
                //  Log.d("TAG", "saveQuestionAndOptionsToDb: question -> ${question.questionDisplay}")
                tagReferenceEntityDao.deleteTagReferenceEntityForTag(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    referenceId = question.questionId ?: -1,
                    referenceType = LanguageAttributeReferenceType.QUESTION.name
                )
                tagReferenceEntityDao.addTagReferenceEntity(
                    TagReferenceEntity.getTagReferenceEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        referenceId = question.questionId ?: -1,
                        tags = question.attributeTag ?: listOf(),
                        referenceType = LanguageAttributeReferenceType.QUESTION.name
                    )
                )
                questionEntityDao.insertQuestion(
                    QuestionEntity.getQuestionEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        isCondition = isSubQuestionList,
                        question = question,
                        parentId = parentId,
                        surveyId = surveyResponseModel.surveyId,
                        sectionId = section.sectionId
                    )
                )

                deleteSurveyLanguageAttribute(
                    question.questionId ?: 0, LanguageAttributeReferenceType.QUESTION.name
                )

                saveSurveyLanguageAttributes(
                    question.surveyLanguageAttributes,
                    question.questionId ?: 0,
                    LanguageAttributeReferenceType.QUESTION.name
                )
                question.options?.forEach { optionsItem ->
                    if (optionsItem != null) {
                        optionItemDao.deleteSurveySectionQuestionOptionFroLanguage(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            optionsItem.optionId!!,
                            question.questionId!!,
                            section.sectionId,
                            surveyResponseModel.surveyId
                        )
                        deleteSurveyLanguageAttribute(
                            optionsItem.optionId ?: 0,
                            LanguageAttributeReferenceType.OPTION.name
                        )

                        saveSurveyLanguageAttributes(
                            optionsItem.surveyLanguageAttributes,
                            optionsItem.optionId ?: 0,
                            LanguageAttributeReferenceType.OPTION.name
                        )

                        optionItemDao.insertOption(
                            OptionItemEntity.getOptionItemEntity(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                optionsItem = optionsItem,
                                sectionId = section.sectionId,
                                surveyId = surveyResponseModel.surveyId,
                                questionId = question.questionId,
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

enum class LanguageAttributeReferenceType {
    OPTION,
    QUESTION,
    SECTION,
    SURVEY

}