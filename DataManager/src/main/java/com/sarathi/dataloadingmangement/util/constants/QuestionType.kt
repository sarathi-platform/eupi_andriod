package com.sarathi.dataloadingmangement.util.constants

enum class QuestionType {
    InputNumber,
    DateType,
    MultiImage,
    SingleSelectDropDown,
    MultiSelectDropDown,
    ToggleGrid,
    MultiSelect,
    AutoCalculation,
    RadioButton,
    Grid,
    Toggle,
    DropDown,
    TextField,
    NumericField,
    InputText,
    IncrementDecrementList;

    companion object {
        val singleResponseQuestionTypeQuestions = listOf(
            SingleSelectDropDown.name.toLowerCase(),
            RadioButton.name.toLowerCase(),
            Toggle.name.toLowerCase(),
            DropDown.name.toLowerCase()
        )

        val multipleResponseQuestionTypeQuestions = listOf(
            MultiSelectDropDown.name.toLowerCase(),
            MultiSelect.name.toLowerCase(),
            ToggleGrid.name.toLowerCase(),
            Grid.name.toLowerCase(),
            IncrementDecrementList.name.toLowerCase()
        )

        val userInputQuestionTypeList = listOf(
            InputNumber.name.toLowerCase(),
            DateType.name.toLowerCase(),
            TextField.name.toLowerCase(),
            InputText.name.toLowerCase(),
            NumericField.name.toLowerCase()
        )

        val numericUseInputQuestionTypeList = listOf(
            InputNumber.name.toLowerCase(),
            NumericField.name.toLowerCase()
        )
    }
}