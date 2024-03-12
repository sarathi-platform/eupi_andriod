package com.nrlm.baselinesurvey.ui.Constants

enum class QuestionType {
    RadioButton,
    SingleSelect,
    MultiSelect,
    Form,
    List,
    Grid,
    Input,
    InputText,
    SingleSelectDropdown,
    SingleSelectDropDown,
    MultiSelectDropdown,
    MultiSelectDropDown,
    Toggle,
    InputNumber,
    InputNumberEditText,
    Calculation;

    companion object {
        fun getQuestionTypeFromName(name: String): QuestionType? {
            return when (name) {
                RadioButton.name -> RadioButton
                SingleSelect.name -> SingleSelect
                MultiSelect.name -> MultiSelect
                Form.name -> Form
                List.name -> List
                Grid.name -> Grid
                Input.name -> Input
                InputText.name -> InputText
                InputNumber.name -> InputNumber
                InputNumberEditText.name -> InputNumberEditText
                SingleSelectDropDown.name -> SingleSelectDropDown
                SingleSelectDropdown.name -> SingleSelectDropdown
                MultiSelectDropdown.name -> MultiSelectDropdown
                MultiSelectDropDown.name -> MultiSelectDropDown
                Toggle.name -> Toggle
                Calculation.name -> Calculation
                else -> {
                    null
                }
            }
        }
    }

}

 enum class ResultType {
     Options,
     Questions,
     Formula
 }