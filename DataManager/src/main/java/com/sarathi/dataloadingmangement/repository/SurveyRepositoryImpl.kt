package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.DEFAULT_ID
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.QuestionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SectionEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.SurveyConfigEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
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
    private val sectionEntityDao: SectionEntityDao,
    val coreSharedPrefs: CoreSharedPrefs,
    val contentDao: ContentDao,
    private val surveyConfigDao: SurveyConfigEntityDao,
) : ISurveyRepository {
    override suspend fun getQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        missionId: Int,
        activityId: Int,
        isFromRegenerate: Boolean
    ): List<QuestionUiModel> {


        val questionUiList = ArrayList<QuestionUiModel>()
        val surveyName = surveyEntityDao.getSurveyDetailForLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId,
        )?.surveyName
        val optionItems = optionItemDao.getSurveySectionQuestionOptionsForLanguage(
            languageId = if (isFromRegenerate) DEFAULT_LANGUAGE_ID.toString() else coreSharedPrefs.getAppLanguage(),
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
        val sectionEntity = sectionEntityDao.getSurveySectionForLanguage(
            surveyId = surveyId,
            sectionId = sectionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )

        val questionList = questionDao.getSurveySectionQuestionForLanguage(
            languageId = if (isFromRegenerate) DEFAULT_LANGUAGE_ID.toString() else coreSharedPrefs.getAppLanguage(),
            sectionId = sectionId,
            surveyId = surveyId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceType = LanguageAttributeReferenceType.QUESTION.name
        )
        val surveyConfigList = surveyConfigDao.getSurveyConfigForSurvey(
            surveyId = surveyId,
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId,
            language = DEFAULT_LANGUAGE_CODE
        )
        questionList.forEach {


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
                formId = it.formId ?: DEFAULT_ID,
                order = it.order.value(0),
                isConditional = it.isConditional,
                sectionName = sectionEntity.sectionName,
                formDescriptionInEnglish = getFormDescription(surveyConfigList, it),
                contentEntities = setQuestionContentData(questionEntity = it)
            )
            questionUiList.add(questionUiModel)

        }

        return questionUiList.sortedBy { it.order }
    }

    private fun getFormDescription(
        surveyConfigList: List<SurveyConfigEntity>,
        it: QuestionUiEntity
    ) = surveyConfigList.filter { it.key == "FORM_QUESTION_CARD_TITLE" }
        .find { surveyConfigFormId -> surveyConfigFormId.formId == it.formId }?.value

    suspend fun setQuestionContentData(questionEntity: QuestionUiEntity): List<ContentList> {
        val contentList = mutableListOf<ContentList>()
        questionEntity.contentEntities?.forEach { data ->
            // Fetch content from database based on content key and language ID
            val contentKey = data.contentKey ?: BLANK_STRING
            val languageId = coreSharedPrefs.getSelectedLanguageCode()

            val contentData = contentDao.getContentFromIds(
                contentkey = contentKey,
                languageId = languageId
            )

            // Add to the content list if contentData is not null
            if (contentData != null) {
                contentList.add(
                    ContentList(
                        contentValue = contentData.contentValue ?: "",
                        contentType = contentData.contentType ?: "",
                        contentKey = contentKey
                    )
                )
            }
        }
        return contentList
    }


    override suspend fun getFormQuestion(
        surveyId: Int,
        subjectId: Int,
        sectionId: Int,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        formId: Int,
        missionId: Int,
        activityId: Int
    ): List<QuestionUiModel> {
        return getQuestion(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = subjectId,
            referenceId = referenceId,
            activityConfigId = activityConfigId,
            grantId = grantId,
            missionId = missionId,
            activityId = activityId,
            isFromRegenerate = false
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