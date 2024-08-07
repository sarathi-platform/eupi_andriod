package com.nudge.incomeexpensemodule.ui.add_event_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.commonUi.CustomDatePickerTextFieldComponent
import com.nudge.core.ui.commonUi.IncrementDecrementNumberComponent
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonNegative
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.componet_.component.InputComponent
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.white
import com.nudge.incomeexpensemodule.ui.component.TypeDropDownComponent
import com.nudge.incomeexpensemodule.viewmodel.AddEventViewModel
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.util.event.InitDataEvent


@Composable
fun AddEventScreen(
    navController: NavHostController = rememberNavController(),
    subjectId: Int,
    subjectName: String,
    viewModel: AddEventViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitAddEventState(subjectId))


    }
    ToolBarWithMenuComponent(
        title = "Asset Purchase",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = {
            navController.navigateUp()
        },
        onSearchValueChange = {},
        onBottomUI = {

            BottomAppBar(
                modifier = Modifier.height(dimen_72_dp),
                backgroundColor = white
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp),
                ) {

                    ButtonNegative(
                        modifier = Modifier.weight(0.5f),
                        buttonTitle = "Delete",
                        textColor = red,
                        isArrowRequired = false,
                        isActive = true,
                        onClick = {
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                    ButtonPositive(modifier = Modifier.weight(0.5f),
                        buttonTitle = "Save",
                        isActive = true,
                        isArrowRequired = false,
                        onClick = { }
                    )

                }
            }
        },
        onSettingClick = {},
        onRetry = {},
        onContentUI = { paddingValues, b, function ->
            Column(
                modifier = Modifier.padding(
                    horizontal = dimen_16_dp
                )
            ) {


                CustomDatePickerTextFieldComponent(
                    isMandatory = true,
                    defaultValue = BLANK_STRING,
                    title = "Date",
                    isEditable = true,
                    hintText = "Select" ?: BLANK_STRING,
                    onDateSelected = {

                    }
                )

                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = "Livelihood",
                    isMandatory = true,
                    sources = viewModel.livelihoodDropdownValue,
                    onAnswerSelection = { selectedValue ->
                        viewModel.onLivelihoodSelect(selectedValue.id)
                    }
                )
                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = "Events*",
                    isMandatory = true,
                    sources = viewModel.livelihoodEventDropdownValue,
                    onAnswerSelection = { selectedValue ->

                    }
                )

                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = "Type of Asset*",
                    isMandatory = true,
                    sources = viewModel.livelihoodAssetDropdownValue,
                    onAnswerSelection = { selectedValue ->
                    }
                )
                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = "Products*",
                    isMandatory = true,
                    sources = viewModel.livelihoodProductDropdownValue,
                    onAnswerSelection = { selectedValue ->
                    }
                )
                InputComponent(
                    maxLength = 7,
                    isMandatory = true,
                    isEditable = true,
                    defaultValue = BLANK_STRING,
                    title = "Amount",
                    isOnlyNumber = true,
                    hintText = BLANK_STRING
                ) { selectedValue, remainingAmout ->

                }
                IncrementDecrementNumberComponent(
                    isMandatory = true,
                    title = "Increase in Number*",
                    isEditAllowed = true,
                    currentValue = "0",
                    onAnswerSelection = { inputValue ->
                    }
                )
            }


        }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddEventScreenPreview() {
    AddEventScreen(subjectId = 0, subjectName = "")
}