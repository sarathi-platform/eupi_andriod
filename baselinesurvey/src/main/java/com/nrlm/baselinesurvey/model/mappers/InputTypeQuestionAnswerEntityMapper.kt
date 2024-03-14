package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.mappers.OptionEntityMapper.getOptionEntitiesMapper
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel

object InputTypeQuestionAnswerEntityMapper {

    fun getInputTypeQuestionAnswerEntity(
        questionResponseModel: QuestionAnswerResponseModel,
        questionEntity: QuestionEntity?
    ): List<InputTypeQuestionAnswerEntity> {
        val inputTypeQuestionAnswerEntities = ArrayList<InputTypeQuestionAnswerEntity>()
        val optionItemEntities =
            getOptionEntitiesMapper(questionResponseModel, questionEntity = questionEntity)

        optionItemEntities.forEach { optionItemEntity ->
            inputTypeQuestionAnswerEntities.add(
                InputTypeQuestionAnswerEntity(
            id = 0,
            didiId = questionResponseModel.subjectId,
            inputValue = optionItemEntity.selectedValue ?: BLANK_STRING,
            optionId = optionItemEntity.optionId ?: DEFAULT_ID,
            sectionId = questionResponseModel.sectionId.toInt(),
            surveyId = questionResponseModel.surveyId,
            questionId = questionResponseModel.question?.questionId ?: -1
                )
            )
    }
        return inputTypeQuestionAnswerEntities
    }



}