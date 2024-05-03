package com.nrlm.baselinesurvey.model.mappers

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.model.response.QuestionOptionsResponseModel
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.utils.convertEventValueFromMultiSelectDropDownEvent

object OptionEntityMapper {
    fun getOptionEntityMapper(
        questionAnswerResponseModel: QuestionAnswerResponseModel,
        questionEntity: QuestionEntity?
    ): OptionItemEntity {
        val questionOptionsResponseModels =
            toQuestionOptionModel(questionAnswerResponseModel.question?.options as List<Any>)

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
        questionEntity: QuestionEntity?,
        optionItemEntityList: List<OptionItemEntity>
    ): List<OptionItemEntity> {
        val questionOptionsResponseModels =
            toQuestionOptionModel(questionAnswerResponseModel.question?.options as List<Any>)
        val optionsList: ArrayList<OptionItemEntity> = ArrayList()

        questionOptionsResponseModels.forEach { questionOptionsResponseModels ->
            val dropDownDownValues =
                optionItemEntityList.find { it.optionId == questionOptionsResponseModels.optionId }
            optionsList.add(
                OptionItemEntity(
                    id = 0,
                    sectionId = questionAnswerResponseModel.sectionId.toInt(),
                    surveyId = questionAnswerResponseModel.surveyId ?: -1,
                    questionId = questionAnswerResponseModel.question?.questionId,
                    optionId = questionOptionsResponseModels.optionId,
                    display = questionEntity?.questionDisplay,
                    weight = null,
                    selectedValue = if (TextUtils.equals(
                            dropDownDownValues?.optionType?.toLowerCase(),
                            QuestionType.MultiSelectDropDown.name.toLowerCase()
                        )
                    ) convertEventValueFromMultiSelectDropDownEvent(
                        questionOptionsResponseModels.selectedValue ?: BLANK_STRING
                    ) else questionOptionsResponseModels.selectedValue,
                    summary = questionEntity?.questionSummary,
                    count = questionEntity?.order,
                    optionType = BLANK_STRING,
                    conditional = questionEntity?.isConditional ?: false,
                    order = questionEntity?.order ?: DEFAULT_ID,
                    languageId = questionEntity?.languageId,
                    isSelected = false,
                    selectedValueId = if (!dropDownDownValues?.values.isNullOrEmpty()) dropDownDownValues?.values?.find {
                        it.value.contains(
                            questionOptionsResponseModels.selectedValue ?: BLANK_STRING,
                            true
                        )
                    }?.id ?: 0 else 0
                )
            )
        }
        return optionsList
    }

    fun getOptionEntitiesMapperForForm(
        questionAnswerResponseModel: QuestionAnswerResponseModel,
    ): List<QuestionOptionsResponseModel> {
        val questionOptionsResponseModels =
            formQuestionOptionModel(questionAnswerResponseModel.question?.options as List<List<Any>>)
        val optionsList: ArrayList<QuestionOptionsResponseModel> = ArrayList()

        questionOptionsResponseModels.forEach { item ->
            item.forEach { questionOptionsResponseModel ->
                optionsList.add(questionOptionsResponseModel)
            }
        }
        return optionsList
    }

    fun toQuestionOptionModel(list: List<Any>): List<QuestionOptionsResponseModel> {
        val type =
            object : TypeToken<List<QuestionOptionsResponseModel?>?>() {}.type
        val gson = Gson()

        return gson.fromJson(gson.toJson(list), type)
    }

    fun formQuestionOptionModel(list: List<List<Any>>): List<List<QuestionOptionsResponseModel>> {
        val type =
            object : TypeToken<List<List<QuestionOptionsResponseModel?>?>>() {}.type
        val gson = Gson()
        return gson.fromJson(gson.toJson(list), type)
    }
}