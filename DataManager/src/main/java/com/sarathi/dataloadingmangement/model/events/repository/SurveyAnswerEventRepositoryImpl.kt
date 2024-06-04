package com.sarathi.dataloadingmangement.model.events.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventOptionItemDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventQuestionItemDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveyAnswerEventRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs
) :
    ISurveyAnswerEventRepository {

    override suspend fun writeSaveAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        refrenceId: Int,
        taskLocalId: String
    ): SaveAnswerEventDto {

        return SaveAnswerEventDto(
            surveyId = questionUiModel.surveyId,
            dateCreated = System.currentTimeMillis(),
            languageId = questionUiModel.languageId,
            subjectId = subjectId,
            subjectType = subjectType,
            sectionId = questionUiModel.sectionId,
            question = getQuestionEvent(questionUiModel),
            referenceId = refrenceId,
            localTaskId = taskLocalId ?: BLANK_STRING
        )


    }


    private fun getQuestionEvent(questionUiModel: QuestionUiModel): SaveAnswerEventQuestionItemDto {
        return SaveAnswerEventQuestionItemDto(
            questionId = questionUiModel.questionId,
            questionType = questionUiModel.type,
            tag = questionUiModel.tagId,
            showQuestion = true,
            questionDesc = questionUiModel.questionSummary ?: BLANK_STRING,
            options = getOption(questionUiModel.options!!, "")
        )
    }

    private fun getOption(
        optionsItems: List<OptionItemEntity>,
        referenceId: String
    ): List<SaveAnswerEventOptionItemDto> {
        val result = ArrayList<SaveAnswerEventOptionItemDto>()
        optionsItems.forEach { optionItem ->
            result.add(
                SaveAnswerEventOptionItemDto(
                    optionId = optionItem.optionId ?: 0,
                    selectedValue = optionItem.selectedValue,
                    tag = optionItem.optionTag,
                    optionDesc = optionItem.summary ?: BLANK_STRING,
                    referenceId = referenceId

                )
            )

        }

        return result
    }
}