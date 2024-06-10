package com.sarathi.dataloadingmangement.repository

import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(
    private val questionDao: QuestionEntityDao,
    private val surveyAnswersDao: SurveyAnswersDao,
    private val optionItemDao: OptionItemDao,
    private val surveyEntityDao: SurveyEntityDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    ISurveyRepository {
    override suspend fun getQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int
    ): List<QuestionUiModel> {


        val questionUiList = ArrayList<QuestionUiModel>()
        val surveyName = surveyEntityDao.getSurveyDetailForLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId,
        )?.surveyName
        val optionItems = optionItemDao.getSurveySectionQuestionOptionsForLanguage(
            languageId = coreSharedPrefs.getAppLanguage(),
            sectionId = sectionId,
            surveyId = surveyId,
            referenceType = LanguageAttributeReferenceType.OPTION.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
        val surveyAnswerList = surveyAnswersDao.getSurveyAnswers(
            sectionId = sectionId,
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )

        questionDao.getSurveySectionQuestionForLanguage(
            languageId = coreSharedPrefs.getAppLanguage(),
            sectionId = sectionId,
            surveyId = surveyId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceType = LanguageAttributeReferenceType.QUESTION.name
        ).forEach {

            val questionUiModel = QuestionUiModel(
                questionId = it.questionId ?: DEFAULT_ID,
                surveyId = it.surveyId,
                sectionId = it.sectionId,
                display = it.description ?: BLANK_STRING,
                languageId = it.languageCode ?: BLANK_STRING,
                questionSummary = it.description,
                questionDisplay = it.description ?: BLANK_STRING,
                type = it.type ?: BLANK_STRING,
                options = getOptionItemsForQuestion(
                    it.questionId ?: DEFAULT_ID,
                    optionItems,
                    surveyAnswerList
                ),
                isMandatory = it.isMandatory,
                tagId = it.tag,
                surveyName = surveyName ?: BLANK_STRING
            )
            questionUiList.add(questionUiModel)

        }

        return questionUiList
    }

    private fun getOptionItemsForQuestion(
        questionId: Int,
        optionItems: List<OptionsUiModel>,
        surveyAnswerList: List<SurveyAnswerEntity>
    ): List<OptionsUiModel> {
        val optionList = optionItems.filter { it.questionId == questionId }
        val surveyAnswer = surveyAnswerList.filter { it.questionId == questionId }
        if (surveyAnswerList.isNotEmpty() && surveyAnswer.isNotEmpty()) {
            val optionItemWithSaved = ArrayList<OptionsUiModel>()
            // if answer exist
            optionList.forEach { questionOptionItem ->

                optionItemWithSaved.add(surveyAnswer.firstOrNull()?.optionItems?.find { it.optionId == questionOptionItem.optionId }
                    ?: questionOptionItem)
            }


            return optionItemWithSaved


        } else {
            return optionList
    }


    }


}