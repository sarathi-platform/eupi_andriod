package com.nrlm.baselinesurvey.ui.Constants

enum class QuestionType {
    RadioButton,   //done
    SingleSelect,   // not
    MultiSelect,
    Form,              //didi detail same
    List,   // not
    Grid,    //done
    Input,    //h
    InputText,   //h
    SingleSelectDropdown,   //h
    SingleSelectDropDown,    //h
    MultiSelectDropdown,    //h
    MultiSelectDropDown,     //h
    Toggle,                //done
    InputNumber,               //h
    InputNumberEditText,        //h
    Calculation,          //autocalculation same
    SingleValueForm,       // not availble
    DidiDetails,                 //done
    YesNoButton,              //not availble
    PhoneNumber,                //not availble
    HrsMinPicker,          //hh
    YrsMonthPicker,        //h
    Image,                    //ask
    FormWithNone,                 //done
    AutoCalculation;              //done


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
                DidiDetails.name -> DidiDetails
                YesNoButton.name -> YesNoButton
                PhoneNumber.name -> PhoneNumber
                HrsMinPicker.name -> HrsMinPicker
                YrsMonthPicker.name -> YrsMonthPicker
                Image.name -> Image
                FormWithNone.name -> FormWithNone
                AutoCalculation.name -> AutoCalculation
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
     Formula,
     NoneMarked,
     Calculation
 }