package com.sarathi.surveymanager.utils.conditions

import com.nudge.core.BLANK_STRING
import com.nudge.core.ifNotEmpty
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.survey.response.Conditions
import com.sarathi.dataloadingmangement.model.uiModel.ConditionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType

class ConditionsUtils private constructor() {

    private var sourceTargetMap: Map<Int, List<Int>> = mapOf()

    private var questionConditionMap: Map<Int, List<Conditions>> = mapOf()

    private var responseMap: Map<Int, List<Int>> = mapOf()

    private var conditionsUiModelList: List<ConditionsUiModel> = listOf()

    fun setConditionsUiModelList(conditionsUiModelList: List<ConditionsUiModel>) {
        this.conditionsUiModelList = conditionsUiModelList
    }

    fun setSourceTargetMap(conditionsUiModelList: List<ConditionsUiModel>) {
        val map: HashMap<Int, List<Int>> = hashMapOf()
        conditionsUiModelList.groupBy { it.sourceQuestionId }.forEach {
            map[it.key] = it.value.map { it.targetQuestionId }
        }

        sourceTargetMap = map
    }

    fun setQuestionConditionMap(conditionsUiModelList: List<ConditionsUiModel>) {
        val map: HashMap<Int, List<Conditions>> = hashMapOf()

        conditionsUiModelList.groupBy { it.targetQuestionId }.forEach { mapEntry ->

            map[mapEntry.key] = mapEntry.value.map { it ->
                Conditions(
                    expression = it.condition,
                    it.sourceQuestionId
                )
            }

        }

        questionConditionMap = map

    }

    fun setResponseMap(questionUiModelList: List<QuestionUiModel>) {
        val map: HashMap<Int, List<Int>> = hashMapOf()
        questionUiModelList
            .filter {
                it.options?.any { opt ->
                    opt.isSelected == true
                            || (opt.selectedValue != BLANK_STRING && opt.selectedValue != null)
                }.value()
            }
            .forEach {
                if (it.type != QuestionType.TextField.name && it.type != QuestionType.InputNumber.name && it.type != QuestionType.NumericField.name) {
                    it.options?.filter { option -> option.isSelected.value() }
                        ?.map { opt -> opt.optionId!! }?.ifNotEmpty { optionIds ->
                            map[it.questionId] = optionIds
                        }
                } else if (it.type != QuestionType.InputNumber.name || it.type != QuestionType.NumericField.name) {
                    it.options?.filter { option -> option.isSelected.value() }
                        ?.map { opt -> opt.selectedValue?.toInt()!! }?.ifNotEmpty { optionIds ->
                            map[it.questionId] = optionIds
                        }

                }

            }
        responseMap = map
    }

    companion object {
        private var INSTANCE: ConditionsUtils? = null

        fun getInstance(): ConditionsUtils {
            if (INSTANCE == null)
                INSTANCE = ConditionsUtils()

            return INSTANCE!!

        }
    }


}
