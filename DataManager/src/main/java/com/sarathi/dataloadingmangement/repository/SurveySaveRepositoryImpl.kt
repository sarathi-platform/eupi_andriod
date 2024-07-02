package com.sarathi.dataloadingmangement.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.MODE_TAG
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
            tagId.toInt(),
            coreSharedPrefs.getUniqueUserIdentifier()
        )
        if (tagId == DISBURSED_AMOUNT_TAG || tagId == NO_OF_POOR_DIDI_TAG || tagId == RECEIVED_AMOUNT_TAG) {
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
        return result.joinToString(",")
    }

    override suspend fun getSurveyAnswerForFormTag(
        taskId: Int,
        subjectId: Int,
        tagId: String,
        referenceId: String
    ): String {
        val result = ArrayList<String>()

        val surveyAnswerEntity = surveyAnswersDao.getSurveyAnswerForFormTag(
            taskId = taskId,
            subjectId = subjectId,
            referenceId = referenceId,
            tagId = tagId.toInt(),
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        )
        if (tagId == DISBURSED_AMOUNT_TAG || tagId == NO_OF_POOR_DIDI_TAG || tagId == RECEIVED_AMOUNT_TAG) {
            var totalAmount = 0
            surveyAnswerEntity?.optionItems?.forEach {
                totalAmount += it.selectedValue?.toInt() ?: 0
            }


            result.add(totalAmount.toString())
        } else {

            surveyAnswerEntity?.optionItems?.forEach {
                if (it.isSelected == true) {
                    if (it.selectedValue?.isNotBlank() == true) {
                        result.add(it.selectedValue ?: BLANK_STRING)
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
        return result.joinToString(",")
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
        surveyId: Int,
        taskId: Int,
        sectionId: Int
    ): List<SurveyAnswerFormSummaryUiModel> {
        return surveyAnswersDao.getSurveyAnswersForSummary(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId
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
        tag: Int
    ): List<OptionsUiModel> {

        val grantConfig =
            grantConfigDao.getGrantConfigWithGrantId(activityConfigId, grantId = grantId)
        val modeOrNatureOptions = ArrayList<OptionsUiModel>()
        val type = object : TypeToken<List<OptionsItem?>?>() {}.type
        val options = Gson().fromJson<List<OptionsItem>>(
            if (tag.toString() == MODE_TAG) grantConfig?.grantMode else grantConfig?.grantNature,
            type
        )
        options?.forEach { option ->
            modeOrNatureOptions.add(
                OptionsUiModel(
                    sectionId = sectionId,
                    surveyId = surveyId,
                    questionId = questionId,
                    optionId = option?.optionId,
                    optionTag = option?.tag ?: DEFAULT_ID,
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