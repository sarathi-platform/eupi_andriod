package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel

object FormQuestionEntityMapper {
    fun getFormQuestionEntity(
        questionResponseModel: QuestionAnswerResponseModel,
        questionEntity: QuestionEntity?
    ): FormQuestionResponseEntity {
        val optionItemEntity = OptionEntityMapper.getOptionEntityMapper(
            questionResponseModel,
            questionEntity = questionEntity
        )
        return FormQuestionResponseEntity(
            id = 0,
            didiId = questionResponseModel.subjectId,
            referenceId = "",
            optionId = optionItemEntity.optionId ?: DEFAULT_ID,
            sectionId = questionResponseModel.sectionId.toInt(),
            surveyId = questionResponseModel.surveyId,
            selectedValue = optionItemEntity.selectedValue ?: BLANK_STRING,
            questionId = questionResponseModel.question?.questionId ?: -1
        )
    }
}