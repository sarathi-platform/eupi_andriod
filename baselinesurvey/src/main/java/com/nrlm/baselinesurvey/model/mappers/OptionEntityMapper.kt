package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.model.response.QuestionOptionsResponseModel

object OptionEntityMapper {
    fun getOptionEntityMapper(
        questionAnswerResponseModel: QuestionAnswerResponseModel,
        questionEntity: QuestionEntity?
    ): OptionItemEntity {
        val questionOptionsResponseModels =
            questionAnswerResponseModel.question?.options as List<QuestionOptionsResponseModel>

        return OptionItemEntity(
            id = 0,
            sectionId = questionAnswerResponseModel.sectionId.toInt() ?: -1,
            surveyId = questionAnswerResponseModel.surveyId ?: -1,
            questionId = questionAnswerResponseModel.question?.questionId,
            optionId = questionOptionsResponseModels.first().optionId,
            display = questionEntity?.questionDisplay,
            weight = null,
            selectedValue = questionOptionsResponseModels.first().selectedValue,
            // optionValue = questionEntity.,
            summary = questionEntity?.questionSummary,
            count = questionEntity?.order,
            optionType = BLANK_STRING,
            conditional = questionEntity?.isConditional ?: false,
            order = questionEntity?.order ?: DEFAULT_ID,
            languageId = questionEntity?.languageId,
            isSelected = false


        )
    }

    fun getOptionEntitiesMapper(
        questionAnswerResponseModel: QuestionAnswerResponseModel,
        questionEntity: QuestionEntity?
    ): List<OptionItemEntity> {
        val questionOptionsResponseModels =
            questionAnswerResponseModel.question?.options as List<QuestionOptionsResponseModel>
        val optionsList: ArrayList<OptionItemEntity> = ArrayList()

        questionOptionsResponseModels.forEach { questionOptionsResponseModels ->
            OptionItemEntity(
                id = 0,
                sectionId = questionAnswerResponseModel.sectionId.toInt(),
                surveyId = questionAnswerResponseModel.surveyId ?: -1,
                questionId = questionAnswerResponseModel.question?.questionId,
                optionId = questionOptionsResponseModels.optionId,
                display = questionEntity?.questionDisplay,
                weight = null,
                selectedValue = questionOptionsResponseModels.selectedValue,
                // optionValue = questionOptionsResponseModels.select,
                summary = questionEntity?.questionSummary,
                count = questionEntity?.order,
                optionType = BLANK_STRING,
                conditional = questionEntity?.isConditional ?: false,
                order = questionEntity?.order ?: DEFAULT_ID,
                languageId = questionEntity?.languageId,
                isSelected = false


            )
        }
        return optionsList
    }
}