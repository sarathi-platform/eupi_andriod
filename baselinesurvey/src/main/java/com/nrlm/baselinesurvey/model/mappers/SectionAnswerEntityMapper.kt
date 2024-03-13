package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.model.mappers.OptionEntityMapper.getOptionEntitiesMapper
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel

object SectionAnswerEntityMapper {
    fun getSectionAnswerEntity(
        questionResponseModel: QuestionAnswerResponseModel,
        question: QuestionEntity?
    ): SectionAnswerEntity {
        return SectionAnswerEntity(
            questionId = questionResponseModel.question?.questionId ?: -1,
            id = 0,
            didiId = questionResponseModel.subjectId,
            questionType = questionResponseModel.question?.questionType ?: "",
            needsToPost = false,
            optionItems = getOptionEntitiesMapper(questionResponseModel, question),
            sectionId = questionResponseModel.sectionId.toInt(),
            surveyId = questionResponseModel.surveyId

        )
    }
}