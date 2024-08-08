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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.getDate
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
import com.nudge.core.value
import com.nudge.incomeexpensemodule.ui.component.TypeDropDownComponent
import com.nudge.incomeexpensemodule.ui.component.rememberSearchBarWithDropDownState
import com.nudge.incomeexpensemodule.viewmodel.AddEventViewModel
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.util.event.InitDataEvent


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEventScreen(
    navController: NavHostController = rememberNavController(),
    subjectId: Int,
    subjectName: String,
    transactionId: String,
    viewModel: AddEventViewModel = hiltViewModel()
) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitAddEventState(subjectId, transactionId))
    }

    val dropDownWithSearchState = remember(viewModel.livelihoodEventDropdownValue) {
        rememberSearchBarWithDropDownState<ValuesDto>(
            dropdownMenuItemList = viewModel.livelihoodEventDropdownValue
        )
    }

    ToolBarWithMenuComponent(
        title = "Asset Purchase",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { /*TODO*/ },
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
                        onClick = {
                            viewModel.onSubmitButtonClick(subjectId, transactionId)
                        }
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
                    defaultValue = viewModel.selectedDate.value,
                    title = "Date",
                    isEditable = true,
                    hintText = "Select" ?: BLANK_STRING,
                    onDateSelected = { date ->
                        viewModel.selectedDate.value = date.value().getDate()
                        viewModel.selectedDateInLong = date.value()

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

//                SearchBarWithDropdownComponent<ValuesDto, AnnotatedString>(
//                    title = TextProperties.getBasicTextProperties(text = buildAnnotatedString {
//                        withStyle(
//                            style = SpanStyle(
//                                color = blueDark,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.SemiBold,
//                                fontFamily = NotoSans
//                            )
//                        ) {
//                            append("Event")
//                        }
//
//                        withStyle(
//                            style = SpanStyle(
//                                color = red,
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.SemiBold,
//                                fontFamily = NotoSans
//                            )
//                        ) {
//                            append("*")
//                        }
//                    }),
//                    paddingValues = PaddingValues(dimen_0_dp),
//                    contentPadding = PaddingValues(dimen_8_dp),
//                    state = dropDownWithSearchState,
//                    onGlobalPositioned = { coordinates ->
//                        textFieldSize = coordinates.size.toSize()
//                    },
//                    onItemSelected = {
//                        dropDownWithSearchState.hide()
//                        dropDownWithSearchState.setSelectedItemValue(dropDownWithSearchState.getFilteredDropDownMenuItemListValue()[it].value)
//                    },
//                    onSearchQueryChanged = { searchQuery ->
//                        dropDownWithSearchState.filterDropDownMenuItemList(dropDownWithSearchState.getDropDownMenuItemListStateValue()) {
//                            it.value.contains(searchQuery.text, true)
//                        }
//                    }
//                ) {
//                    CustomTextViewComponent(
//                        textProperties = TextProperties.getBasicTextProperties(text = it.value)
//                            .copy(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .exposedDropdownSize(),
//                                color = textColorDark
//                            )
//                    )
//                }
                TypeDropDownComponent(
                    isEditAllowed = true,
                    title = "Events",
                    isMandatory = true,
                    sources = viewModel.livelihoodEventDropdownValue,
                    onAnswerSelection = { selectedValue ->
                        viewModel.onEventSelected(selectedValue)
                    }
                )


                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET].value()) {
                    TypeDropDownComponent(
                        isEditAllowed = true,
                        title = "Type of Asset*",
                        isMandatory = true,
                        sources = viewModel.livelihoodAssetDropdownValue,
                        onAnswerSelection = { selectedValue ->
                            viewModel.selectedAssetTypeId.value = selectedValue.id
                        }
                    )
                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT].value()) {

                    TypeDropDownComponent(
                        isEditAllowed = true,
                        title = "Products*",
                        isMandatory = true,
                        sources = viewModel.livelihoodProductDropdownValue,
                        onAnswerSelection = { selectedValue ->
                            viewModel.selectedProductId.value = selectedValue.id

                        }
                    )
                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.COUNT_OF_ASSET].value()) {

                    IncrementDecrementNumberComponent(
                        isMandatory = true,
                        title = "Increase in Number*",
                        isEditAllowed = true,
                        currentValue = viewModel.assetCount.value,
                        onAnswerSelection = { inputValue ->
                            viewModel.assetCount.value = inputValue
                        }
                    )
                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.AMOUNT].value()) {

                    InputComponent(
                        maxLength = 7,
                        isMandatory = true,
                        isEditable = true,
                        defaultValue = BLANK_STRING,
                        title = "Amount",
                        isOnlyNumber = true,
                        hintText = BLANK_STRING
                    ) { selectedValue, remainingAmout ->
                        viewModel.amount.value = selectedValue

                    }
                }

            }


        }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddEventScreenPreview() {
    AddEventScreen(subjectId = 0, subjectName = "", transactionId = "")
}