package com.nrlm.baselinesurvey.model.mappers

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.ui.common_components.SHGFlag
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.tagList

object DidiInfoEntityMapper {

    fun getDidiDidiInfoEntity(
        questionResponseModel: QuestionAnswerResponseModel
    ): DidiIntoEntity {

        val optionItemEntities = OptionEntityMapper.getOptionEntitiesMapperForForm(
            questionResponseModel,
        )
        var didiInfoEntity = DidiIntoEntity.getEmptyDidiIntoEntity()
        optionItemEntities.forEach { questionOptionsResponseModel ->
            if (tagList.findTagForId(questionOptionsResponseModel.tag)
                    ?.equals("Aadhar", true) == true
            ) {
                didiInfoEntity = didiInfoEntity.copy(
                    questionResponseModel.subjectId,
                    isAdharCard = SHGFlag.fromSting(
                        questionOptionsResponseModel.selectedValue ?: BLANK_STRING
                    ).value
                )
            } else if (tagList.findTagForId(questionOptionsResponseModel.tag)
                    ?.equals("Voter", true) == true
            ) {
                didiInfoEntity = didiInfoEntity.copy(
                    questionResponseModel.subjectId,
                    isVoterCard = SHGFlag.fromSting(
                        questionOptionsResponseModel.selectedValue ?: BLANK_STRING
                    ).value
                )
            } else {
                didiInfoEntity = didiInfoEntity.copy(
                    questionResponseModel.subjectId,
                    phoneNumber = questionOptionsResponseModel.selectedValue ?: BLANK_STRING
                )
            }
        }

        return didiInfoEntity
    }

}
