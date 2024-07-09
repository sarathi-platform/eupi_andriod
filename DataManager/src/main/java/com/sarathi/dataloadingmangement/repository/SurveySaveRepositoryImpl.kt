package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DELEGATE_COMM
import com.sarathi.dataloadingmangement.DELEGATE_COMM_WITH_SPACE
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.NO_OF_POOR_DIDI_TAG
import com.sarathi.dataloadingmangement.RECEIVED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.dao.OptionItemDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.survey.response.OptionsItem
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import javax.inject.Inject

class SurveySaveRepositoryImpl @Inject constructor(
    val surveyAnswersDao: SurveyAnswersDao,
    val coreSharedPrefs: CoreSharedPrefs,
    val optionItemDao: OptionItemDao,
    val grantConfigDao: GrantConfigDao
) :
    ISurveySaveRepository {
    override suspend fun saveSurveyAnswer(
        question: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String,
        grantId: Int,
        grantType: String
    ) {

        surveyAnswersDao.insertOrModifySurveyAnswer(
            SurveyAnswerEntity.getSurveyAnswerEntity(
                question = question,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                subjectId = subjectId,
                referenceId = referenceId,
                taskId = taskId,
                grantId = grantId,
                grantType = grantType
            )
        )
    }

    override fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        val result = ArrayList<String>()

        val surveyAnswerEntities = surveyAnswersDao.getSurveyAnswerForTag(
            taskId,
            subjectId,
            coreSharedPrefs.getUniqueUserIdentifier()
        ).filter { it.tagId.contains(tagId.toInt()) }
        if (tagId == DISBURSED_AMOUNT_TAG.toString() || tagId == RECEIVED_AMOUNT_TAG.toString() || tagId == NO_OF_POOR_DIDI_TAG.toString()) {
            var totalAmount = 0
            surveyAnswerEntities.forEach { surveyAnswerEntity ->

                surveyAnswerEntity?.optionItems?.forEach {
                    totalAmount += it.selectedValue?.toInt() ?: 0
                }

            }
            result.add(totalAmount.toString())
        } else {
            surveyAnswerEntities.forEach { surveyAnswerEntity ->

                surveyAnswerEntity?.optionItems?.forEach {
                    if (it.isSelected == true) {
                        if (it.selectedValue?.isNotBlank() == true) {
                            result.add(it.selectedValue ?: BLANK_STRING)
                        } else {
                            result.add(it.description ?: BLANK_STRING)
                        }
                    }
                }

            }
        }
        return result.joinToString(DELEGATE_COMM)
    }

    override suspend fun getSurveyAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: String,
        activityConfigId: Int,
        referenceId: String
    ): String {
        val result = ArrayList<String>()

        val surveyAnswerEntity = surveyAnswersDao.getSurveyAnswerForFormTag(
            taskId = taskId,
            subjectId = subjectId,
            referenceId = referenceId,
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        ).find { it.tagId.contains(tagId.toInt()) }
        if (surveyAnswerEntity?.tagId?.contains(DISBURSED_AMOUNT_TAG) == true || surveyAnswerEntity?.tagId?.contains(
                NO_OF_POOR_DIDI_TAG
            ) == true || surveyAnswerEntity?.tagId?.contains(RECEIVED_AMOUNT_TAG) == true
        ) {
            var totalAmount = 0
            surveyAnswerEntity?.optionItems?.forEach {
                totalAmount += it.selectedValue?.toInt() ?: 0
            }


            result.add(totalAmount.toString())
        } else {

            surveyAnswerEntity?.optionItems?.forEach { optionItem ->
                if (optionItem.isSelected == true) {
                    if (optionItem.selectedValue?.isNotBlank() == true && (surveyAnswerEntity.questionType != QuestionType.SingleSelectDropDown.name && surveyAnswerEntity.questionType != QuestionType.MultiSelectDropDown.name)) {
                        result.add(optionItem.selectedValue ?: BLANK_STRING)
                    } else {
                        val optionUiModelList = getOptionsForModeAndNature(
                            activityConfigId = activityConfigId,
                            grantId = surveyAnswerEntity.grantId,
                            tag = surveyAnswerEntity.tagId,
                            surveyId = surveyAnswerEntity.surveyId,
                            sectionId = surveyAnswerEntity.sectionId,
                            questionId = surveyAnswerEntity.questionId
                        )
                        val optionUiModel =
                            optionUiModelList.find { it.questionId == surveyAnswerEntity.questionId && it.optionId == optionItem.optionId }
                        if (optionUiModel != null) {
                            optionItem.description = optionUiModel.description
                            result.add(optionItem.description ?: BLANK_STRING)
                        } else {
                            result.add(optionItem.description ?: BLANK_STRING)
                        }
                    }
                }
            }
        }
        return result.joinToString(DELEGATE_COMM_WITH_SPACE)
    }

    override suspend fun getSurveyAnswerImageKeys(
        questionType: String,
    ): List<SurveyAnswerEntity>? {
        return surveyAnswersDao.getSurveyAnswerImageKeys(
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
            questionType = questionType
        )
    }

    override fun getUserIdentifier(): String {
        return coreSharedPrefs.getUniqueUserIdentifier()
    }

    override suspend fun getAllSaveAnswer(
        activityConfigId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        grantId: Int
    ): List<SurveyAnswerFormSummaryUiModel> {
        val surveyAnswers = surveyAnswersDao.getSurveyAnswersForSummary(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId
        )
        surveyAnswers.forEachIndexed { index, surveyAnswerData ->
            if (surveyAnswerData.questionType == QuestionType.MultiSelectDropDown.name || surveyAnswerData.questionType == QuestionType.SingleSelectDropDown.name) {
                if (surveyAnswerData.tagId.contains(MODE_TAG) || surveyAnswerData.tagId.contains(
                        NATURE_TAG
                    )
                ) {
                    val optionUiModelList = getOptionsForModeAndNature(
                        activityConfigId = activityConfigId,
                        grantId = grantId,
                        tag = surveyAnswerData.tagId,
                        surveyId = surveyId,
                        sectionId = sectionId,
                        questionId = surveyAnswerData.questionId
                    )
                    surveyAnswerData.optionItems.forEachIndexed { optionIndex, option ->
                        val optionItem =
                            optionUiModelList.find { it.questionId == surveyAnswerData.questionId && it.optionId == option.optionId }
                        if (optionItem != null) {
                            surveyAnswers[index].optionItems[optionIndex].description =
                                optionItem.description
                        }
                    }
                }
            } else {
                val questionOptions = getSurveySectionQuestionOptionsForLanguage(
                    sectionId = sectionId,
                    surveyId = surveyId,
                    referenceType = surveyAnswerData.referenceId
                )
                surveyAnswerData.optionItems.forEachIndexed { optionIndex, option ->
                    val optionItem =
                        questionOptions.find { it.questionId == surveyAnswerData.questionId && it.optionId == option.optionId }
                    if (optionItem != null) {
                        surveyAnswers[index].optionItems[optionIndex].description =
                            optionItem.description
                    }
                }
            }

        }
        return surveyAnswers
    }

    private fun getSurveySectionQuestionOptionsForLanguage(
        sectionId: Int,
        surveyId: Int,
        referenceType: String,
    ): List<OptionsUiModel> {
        return optionItemDao.getSurveySectionQuestionOptionsForLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            referenceType = referenceType,
            languageId = coreSharedPrefs.getSelectedLanguageCode()
        )
    }


    override suspend fun deleteSurveyAnswer(
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int {
        return surveyAnswersDao.deleteSurveyAnswer(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId,
            referenceId = referenceId
        )
    }

    private fun getOptionsForModeAndNature(
        activityConfigId: Int,
        grantId: Int,
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        tag: List<Int>
    ): List<OptionsUiModel> {

        val grantConfig =
            grantConfigDao.getGrantConfigWithGrantId(activityConfigId, grantId = grantId)
        val modeOrNatureOptions = ArrayList<OptionsUiModel>()
        val type = object : TypeToken<List<OptionsItem?>?>() {}.type
        val options = Gson().fromJson<List<OptionsItem>>(
            if (tag.contains(MODE_TAG)) grantConfig?.grantMode else grantConfig?.grantNature,
            type
        )
        options?.forEach { option ->
            modeOrNatureOptions.add(
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

        return modeOrNatureOptions

    }
}