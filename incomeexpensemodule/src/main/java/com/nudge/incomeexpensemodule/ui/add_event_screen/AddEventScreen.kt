package com.nudge.incomeexpensemodule.ui.add_event_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.incomeexpensemodule.R
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.ui.commonUi.CustomDatePickerTextFieldComponent
import com.nudge.core.ui.commonUi.IncrementDecrementNumberComponent
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonNegative
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.componet_.component.InputComponent
import com.nudge.core.ui.commonUi.componet_.component.ShowCustomDialog
import com.nudge.core.ui.commonUi.rememberCustomDatePickerDialogProperties
import com.nudge.core.ui.commonUi.rememberCustomDatePickerState
import com.nudge.core.ui.commonUi.rememberDatePickerProperties
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
import com.sarathi.dataloadingmangement.INFLOW
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping.Companion.getLivelihoodEventFromName
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.util.event.InitDataEvent


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: AddEventViewModel = hiltViewModel(),
    subjectId: Int,
    subjectName: String,
    transactionId: String,
    showDeleteButton: Boolean = false,
    onSettingClick: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitAddEventState(subjectId, transactionId))
    }

    val dropDownWithSearchState = remember(viewModel.livelihoodEventDropdownValue) {
        rememberSearchBarWithDropDownState<ValuesDto>(
            dropdownMenuItemList = viewModel.livelihoodEventDropdownValue
        )
    }

    val datePickerState =
        rememberCustomDatePickerState()

    val datePickerProperties = rememberDatePickerProperties(
        state = datePickerState,
        dateValidator = {
            it <= getCurrentTimeInMillis()
        }
    )

    val datePickerDialogProperties = rememberCustomDatePickerDialogProperties()

    ToolBarWithMenuComponent(
        title = if (showDeleteButton) "Edit Event" else "Add Event",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.navigateUp() },
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
                    if (showDeleteButton) {
                        ButtonNegative(
                            modifier = Modifier.weight(0.5f),
                            buttonTitle = "Delete",
                            textColor = red,
                            isArrowRequired = false,
                            isActive = true,
                            onClick = {
                                viewModel.showDeleteDialog.value = true
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                    }

                    ButtonPositive(
                        modifier = Modifier.weight(0.5f),
                        buttonTitle = "Save",
                        isActive = viewModel.isSubmitButtonEnable.value,


                        isArrowRequired = false,
                        onClick = {
                            viewModel.onSubmitButtonClick(subjectId, transactionId)
                            navController.navigateUp()
                        }
                    )

                }
            }
        },
        onSettingClick = { onSettingClick() },
        onRetry = {},
        onContentUI = { paddingValues, b, function ->
            LazyColumn(
                modifier = Modifier.padding(
                    horizontal = dimen_16_dp
                )
            ) {


                item {
                    CustomDatePickerTextFieldComponent(
                        isMandatory = true,
                        defaultValue = viewModel.selectedDate.value,
                        title = stringResource(R.string.date),
                        isEditable = true,
                        hintText = stringResource(R.string.select) ?: BLANK_STRING,
                        datePickerState = datePickerState,
                        datePickerProperties = datePickerProperties,
                        datePickerDialogProperties = datePickerDialogProperties,
                        onDateSelected = { date ->
                            viewModel.selectedDate.value = date.value().getDate()
                            viewModel.selectedDateInLong = date.value()
                            viewModel.validateForm()
                        }
                    )
                }

                item {
                    TypeDropDownComponent(
                        isEditAllowed = !showDeleteButton,
                        title = stringResource(R.string.livelihood),
                        isMandatory = true,
                        sources = viewModel.livelihoodDropdownValue,
                        selectedValue = viewModel.livelihoodDropdownValue.find { it.id == viewModel.selectedLivelihoodId.value }?.value,

                        onAnswerSelection = { selectedValue ->
                            viewModel.onLivelihoodSelect(selectedValue.id)
                            viewModel.validateForm()

                        }
                    )

                    //TODO @Anupam fix this before merge.
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
                }



                item {
                    TypeDropDownComponent(
                        isEditAllowed = !showDeleteButton,
                        title = stringResource(R.string.events),
                        isMandatory = true,
                        selectedValue = viewModel.livelihoodEventDropdownValue.find { it.id == viewModel.selectedEventId.value }?.value,
                        sources = viewModel.livelihoodEventDropdownValue,
                        onAnswerSelection = { selectedValue ->
                            viewModel.onEventSelected(selectedValue)
                            viewModel.validateForm()
                        }
                    )
                }

                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET].value()) {
                    item {
                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = stringResource(R.string.type_of_asset),
                            selectedValue = viewModel.livelihoodAssetDropdownValue.find { it.id == viewModel.selectedAssetTypeId.value }?.value,
                            isMandatory = true,
                            sources = viewModel.livelihoodAssetDropdownValue,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedAssetTypeId.value = selectedValue.id
                                viewModel.validateForm()
                            }
                        )
                    }


                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT].value()) {
                    item {
                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = stringResource(R.string.products),
                            isMandatory = true,
                            sources = viewModel.livelihoodProductDropdownValue,
                            selectedValue = viewModel.livelihoodProductDropdownValue.find { it.id == viewModel.selectedProductId.value }?.value,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedProductId.value = selectedValue.id
                                viewModel.validateForm()

                            }
                        )
                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.COUNT_OF_ASSET].value()) {
                    item {
                        val str =
                            if (getLivelihoodEventFromName(viewModel.eventType).assetJournalEntryFlowType?.name?.equals(
                                    INFLOW
                                ) == true
                            ) stringResource(R.string.increase_in_number) else stringResource(R.string.decrease_in_number)
                        IncrementDecrementNumberComponent(
                            isMandatory = true,
                            title = str,
                            isEditAllowed = true,
                            currentValue = viewModel.assetCount.value,
                            onAnswerSelection = { inputValue ->
                                viewModel.assetCount.value = inputValue
                                viewModel.validateForm()
                            }
                        )
                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.AMOUNT].value()) {
                    item {
                        InputComponent(
                            maxLength = 7,
                            isMandatory = true,
                            isEditable = true,
                            defaultValue = viewModel.amount.value,
                            title = stringResource(R.string.amount),
                            isOnlyNumber = true,
                            hintText = BLANK_STRING
                        ) { selectedValue, remainingAmout ->
                            viewModel.amount.value = selectedValue
                            viewModel.validateForm()

                        }
                    }

                }

            }

            if (viewModel.showDeleteDialog.value) {
                ShowCustomDialog(
                    message = stringResource(R.string.are_you_sure_you_want_to_delete),
                    negativeButtonTitle = stringResource(R.string.no),
                    positiveButtonTitle = stringResource(R.string.yes),
                    onNegativeButtonClick = {
                        viewModel.showDeleteDialog.value = false
                    },
                    onPositiveButtonClick = {
                        viewModel.onDeleteClick(transactionId, subjectId)
                        navController.navigateUp()
                        viewModel.showDeleteDialog.value = false

                    }
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddEventScreenPreview() {
    AddEventScreen(subjectId = 0, subjectName = "", transactionId = "", onSettingClick = {})
}