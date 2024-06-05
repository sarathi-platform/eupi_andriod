package com.sarathi.dataloadingmangement.repository

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
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        refrenceId: Int,
        taskLocalId: String
    ): SaveAnswerEventDto {

        return SaveAnswerEventDto(
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


    private fun getQuestionEvent(questionUiModels: List<QuestionUiModel>): List<SaveAnswerEventQuestionItemDto> {
        var questionAnsweList = ArrayList<SaveAnswerEventQuestionItemDto>()
        questionUiModels.forEach { questionUiModel ->


            val options = getOption(questionUiModel.options!!, "")
            if (options.isNotEmpty()) {
                questionAnsweList.add(
                    SaveAnswerEventQuestionItemDto(
            questionId = questionUiModel.questionId,
            questionType = questionUiModel.type,
            tag = questionUiModel.tagId,
            showQuestion = true,
            questionDesc = questionUiModel.questionDisplay ?: BLANK_STRING,
                        options = options
                    )
                )
            }
        }
        return questionAnsweList
    }

    private fun getOption(
        optionsItems: List<OptionItemEntity>,
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
                        optionDesc = optionItem.summary ?: BLANK_STRING,
                        referenceId = referenceId

                    )
                )
            }
        }
        return result

    }
}