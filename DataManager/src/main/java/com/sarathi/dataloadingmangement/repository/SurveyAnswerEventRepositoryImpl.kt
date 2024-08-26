package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.TagReferenceEntityDao
import com.sarathi.dataloadingmangement.model.events.DeleteAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventOptionItemDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventQuestionItemDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import javax.inject.Inject

class SurveyAnswerEventRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    private val tagReferenceEntityDao: TagReferenceEntityDao
) :
    ISurveyAnswerEventRepository {

    override suspend fun writeMoneyJournalSaveAnswerEvent(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        sectionTagId: List<Int>
    ): SaveAnswerMoneyJorunalEventDto {

        return SaveAnswerMoneyJorunalEventDto(
            surveyId = questionUiModels.first().surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = questionUiModels.first().languageId,
            subjectId = subjectId,
            subjectType = subjectType,
            sectionId = questionUiModels.first().sectionId,
            question = getQuestionEvent(questionUiModels),
            transactionId = refrenceId,
            localTaskId = taskLocalId ?: BLANK_STRING,
            grantId = grantId,
            grantType = grantType,
            taskId = taskId,
            tagId = sectionTagId
        )


    }

    override suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int
    ): SaveAnswerEventDto {
        return SaveAnswerEventDto(
            surveyId = questionUiModel.surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = questionUiModel.languageId,
            subjectId = subjectId,
            subjectType = subjectType,
            sectionId = questionUiModel.sectionId,
            question = getSaveAnswerEventQuestionItemDto(questionUiModel)!!,
            referenceId = refrenceId,
            localTaskId = taskLocalId ?: BLANK_STRING,
            grantId = grantId,
            grantType = grantType,
            taskId = taskId


        )

    }

    override suspend fun writeDeleteSaveAnswerEvent(
        surveyID: Int,
        sectionId: Int,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int
    ): DeleteAnswerEventDto {
        return DeleteAnswerEventDto(
            surveyId = surveyID,
            dateCreated = System.currentTimeMillis(),
            languageId = coreSharedPrefs.getAppLanguage(),
            subjectId = subjectId,
            subjectType = subjectType,
            sectionId = sectionId,
            referenceId = refrenceId,
            localTaskId = taskLocalId ?: BLANK_STRING,
            grantId = grantId,
            grantType = grantType,
            taskId = taskId
        )
    }

    override suspend fun getTagIdForSection(sectionId: Int): List<Int> {
        return tagReferenceEntityDao.getTagIds(
            referenceId = sectionId,
            referenceType = LanguageAttributeReferenceType.SECTION.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


    private fun getQuestionEvent(questionUiModels: List<QuestionUiModel>): List<SaveAnswerEventQuestionItemDto> {
        val questionAnsweList = ArrayList<SaveAnswerEventQuestionItemDto>()
        questionUiModels.forEach { questionUiModel ->

            val saveAnswerEventQuestionItemDto =
                getSaveAnswerEventQuestionItemDto(questionUiModel)
            saveAnswerEventQuestionItemDto?.let { questionAnsweList.add(it) }
        }
        return questionAnsweList
    }

    private fun getSaveAnswerEventQuestionItemDto(
        questionUiModel: QuestionUiModel,
    ): SaveAnswerEventQuestionItemDto? {
        var saveAnswerEventQuestionItemDto1: SaveAnswerEventQuestionItemDto? = null
        val options = getOption(questionUiModel, "")

        if (options.isNotEmpty()) {

            saveAnswerEventQuestionItemDto1 = SaveAnswerEventQuestionItemDto(
                questionId = questionUiModel.questionId,
                questionType = questionUiModel.type,
                tag = questionUiModel.tagId,
                showQuestion = true,
                questionDesc = questionUiModel.questionSummary ?: BLANK_STRING,
                options = options,
                formId = questionUiModel.formId
            )

        }
        return saveAnswerEventQuestionItemDto1
    }

    private fun getOption(
        questionUiModel: QuestionUiModel,
        referenceId: String
    ): List<SaveAnswerEventOptionItemDto> {
        val result = ArrayList<SaveAnswerEventOptionItemDto>()
        questionUiModel.options?.forEach { optionItem ->
            if (optionItem.isSelected == true) {

                if (questionUiModel.type == QuestionType.MultiSelectDropDown.name
                    || questionUiModel.type == QuestionType.SingleSelectDropDown.name
                    || questionUiModel.type == QuestionType.MultiSelect.name
                ) {
                    result.add(
                    SaveAnswerEventOptionItemDto(
                        optionId = optionItem.optionId ?: 0,
                        selectedValue = optionItem.description,
                        optionDesc = optionItem.originalValue ?: BLANK_STRING,
                        referenceId = referenceId

                    )
                    )
                } else {
                    result.add(
                        SaveAnswerEventOptionItemDto(
                            optionId = optionItem.optionId ?: 0,
                            selectedValue = optionItem.selectedValue,
                            optionDesc = optionItem.description ?: BLANK_STRING,
                            referenceId = referenceId

                        )
                    )
                }
            }
        }
        return result

    }
}