package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventOptionItemDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventQuestionItemDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveyAnswerEventRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs
) :
    ISurveyAnswerEventRepository {

    override suspend fun writeMoneyJournalSaveAnswerEvent(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String
    ): SaveAnswerMoneyJorunalEventDto {

        return SaveAnswerMoneyJorunalEventDto(
            surveyId = questionUiModels.first().surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = questionUiModels.first().languageId,
            subjectId = subjectId,
            subjectType = subjectType,
            sectionId = questionUiModels.first().sectionId,
            question = getQuestionEvent(questionUiModels),
            referenceId = refrenceId,
            localTaskId = taskLocalId ?: BLANK_STRING
        )


    }

    override suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: String,
        taskLocalId: String
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
            localTaskId = taskLocalId ?: BLANK_STRING
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
        val options = getOption(questionUiModel.options!!, "")

        if (options.isNotEmpty()) {

            saveAnswerEventQuestionItemDto1 = SaveAnswerEventQuestionItemDto(
                questionId = questionUiModel.questionId,
                questionType = questionUiModel.type,
                tag = questionUiModel.tagId,
                showQuestion = true,
                questionDesc = questionUiModel.questionDisplay ?: BLANK_STRING,
                options = options,
                formId = questionUiModel.formId
            )

        }
        return saveAnswerEventQuestionItemDto1
    }

    private fun getOption(
        optionsItems: List<OptionsUiModel>,
        referenceId: String
    ): List<SaveAnswerEventOptionItemDto> {
        val result = ArrayList<SaveAnswerEventOptionItemDto>()
        optionsItems.forEach { optionItem ->
            if (optionItem.isSelected == true) {
                result.add(
                    SaveAnswerEventOptionItemDto(
                        optionId = optionItem.optionId ?: 0,
                        selectedValue = optionItem.selectedValue,
                        tag = optionItem.optionTag,
                        optionDesc = optionItem.description ?: BLANK_STRING,
                        referenceId = referenceId

                    )
                )
            }
        }
        return result

    }
}