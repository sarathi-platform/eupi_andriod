package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.survey.response.OptionsItem
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(
    private val questionDao: QuestionEntityDao,
    private val surveyAnswersDao: SurveyAnswersDao,
    private val optionItemDao: OptionItemDao,
    private val surveyEntityDao: SurveyEntityDao,
    private val grantConfigDao: GrantConfigDao,
    val coreSharedPrefs: CoreSharedPrefs
) : ISurveyRepository {
    override suspend fun getQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int
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
            referenceId = referenceId,
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
                display = it.paraphrase ?: BLANK_STRING,
                languageId = it.languageCode ?: BLANK_STRING,
                questionSummary = it.originalValue,
                questionDisplay = it.description ?: BLANK_STRING,
                type = it.type ?: BLANK_STRING,
                options = getOptionItemsForQuestion(
                    it, optionItems, surveyAnswerList, activityConfigId, grantId
                ),
                isMandatory = it.isMandatory,
                tagId = it.tag,
                surveyName = surveyName ?: BLANK_STRING,
                formId = it.formId ?: DEFAULT_ID
            )
            questionUiList.add(questionUiModel)

        }

        return questionUiList
    }

    override suspend fun getFormQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        formId: Int
    ): List<QuestionUiModel> {
        return getQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId
        ).filter { it.formId == formId }
    }

    private suspend fun getOptionItemsForQuestion(
        question: QuestionUiEntity,
        optionItems: List<OptionsUiModel>,
        surveyAnswerList: List<SurveyAnswerEntity>,
        activityConfigId: Int,
        grantId: Int
    ): List<OptionsUiModel> {
        var optionList = optionItems.filter { it.questionId == question.questionId }
        if (question.tag.contains(MODE_TAG) || question.tag.contains(NATURE_TAG)) {

            optionList = getOptionsForModeAndNature(activityConfigId, grantId, question)
        }

        val surveyAnswer = surveyAnswerList.filter { it.questionId == question.questionId }
        if (surveyAnswerList.isNotEmpty() && surveyAnswer.isNotEmpty()) {
            // if answer exist
            optionList.forEach { questionOptionItem ->
                val savedOption =
                    surveyAnswer.firstOrNull()?.optionItems?.find { it.optionId == questionOptionItem.optionId }
                savedOption?.let { savedOptionAnswer ->
                    questionOptionItem.selectedValue = savedOption.selectedValue
                    questionOptionItem.isSelected = savedOption.isSelected
                }
            }
        }
        return optionList
    }

    private suspend fun getOptionsForModeAndNature(
        activityConfigId: Int, grantId: Int, question: QuestionUiEntity
    ): List<OptionsUiModel> {

        val grantConfig =
            grantConfigDao.getGrantConfigWithGrantId(activityConfigId, grantId = grantId)
        val modeOrNatureOptions = ArrayList<OptionsUiModel>()
        val type = object : TypeToken<List<OptionsItem?>?>() {}.type
        val options = Gson().fromJson<List<OptionsItem>>(
            if (question.tag.contains(MODE_TAG)) grantConfig?.grantMode else grantConfig?.grantNature,
            type
        )
        options?.forEach { option ->
            modeOrNatureOptions.add(
                OptionsUiModel(
                    sectionId = question.sectionId,
                    surveyId = question.surveyId,
                    questionId = question.questionId,
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

        return modeOrNatureOptions

    }


}