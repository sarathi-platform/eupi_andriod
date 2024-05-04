package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.utils.convertEventValueFromMultiSelectDropDownEvent
import com.nrlm.baselinesurvey.utils.isMultiSelectDropdown

object FormQuestionEntityMapper {
    fun getFormQuestionEntity(
        questionResponseModel: QuestionAnswerResponseModel,
        optionsList: List<OptionItemEntity>,
    ): List<FormQuestionResponseEntity> {
        val formEntities = ArrayList<FormQuestionResponseEntity>()
        val optionItemEntities = OptionEntityMapper.getOptionEntitiesMapperForForm(
            questionResponseModel,
        )
        optionItemEntities.forEach { optionItemEntity ->
            formEntities.add(
                FormQuestionResponseEntity(
                    id = 0,
                    didiId = questionResponseModel.subjectId,
                    referenceId = optionItemEntity.referenceId,
                    optionId = optionItemEntity.optionId ?: DEFAULT_ID,
                    sectionId = questionResponseModel.sectionId.toInt(),
                    surveyId = questionResponseModel.surveyId,
                    selectedValue = if (isMultiSelectDropdown(
                            optionsList,
                            optionItemEntity.optionId
                        )
                    ) convertEventValueFromMultiSelectDropDownEvent(
                        optionItemEntity.selectedValue ?: BLANK_STRING
                    ) else optionItemEntity.selectedValue ?: BLANK_STRING,
                    questionId = questionResponseModel.question?.questionId ?: -1
                )

            )
        }
        return formEntities
    }

}