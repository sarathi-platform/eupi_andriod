package com.nudge.incomeexpensemodule.ui.add_event_screen

import android.text.TextUtils
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomDatePickerDialogProperties
import com.nudge.core.ui.commonUi.rememberCustomDatePickerState
import com.nudge.core.ui.commonUi.rememberDatePickerProperties
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.nudge.incomeexpensemodule.ui.component.TypeDropDownComponent
import com.nudge.incomeexpensemodule.ui.component.rememberSearchBarWithDropDownState
import com.nudge.incomeexpensemodule.utils.EVENT_MESSAGE
import com.nudge.incomeexpensemodule.utils.NEWLY_ADDED_EVENT_TRANSACTION_ID
import com.nudge.incomeexpensemodule.utils.SELECTED_LIVELIHOOD_ID
import com.nudge.incomeexpensemodule.viewmodel.AddEventViewModel
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.INFLOW
import com.sarathi.dataloadingmangement.enums.AddEventFieldEnum
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
    BackHandler {
        popBackToPreviousScreen(
            navController,
            viewModel,
            message = BLANK_STRING,
            transactionId = BLANK_STRING
        )
    }
    val context = LocalContext.current


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
        title = if (showDeleteButton) stringResource(R.string.edit_event) else stringResource(R.string.add_event),
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = {
            popBackToPreviousScreen(
                navController,
                viewModel,
                message = BLANK_STRING,
                transactionId = BLANK_STRING
            )
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
                    if (showDeleteButton) {
                        ButtonNegative(
                            modifier = Modifier.weight(0.5f),
                            buttonTitle = viewModel.translationHelper.getString(
                                context,
                                R.string.delete,
                            ),
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
                        buttonTitle = viewModel.translationHelper.getString(
                            context,
                            R.string.save_text,
                        ),
                        isActive = viewModel.isSubmitButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            viewModel.onSubmitButtonClick(subjectId, transactionId) {
                                popBackToPreviousScreen(
                                    navController,
                                    viewModel,
                                    message = context.getString(R.string.event_added_successfully),
                                    transactionId
                                )
                            }
                        }
                    )

                }
            }
        },
        onSettingClick = { onSettingClick() },
        onRetry = {},
        onContentUI = { paddingValues, b, function ->
            LazyColumn(
                modifier = Modifier
                    .padding(
                        horizontal = dimen_16_dp,
                    )
                    .fillMaxWidth()

            ) {


                item {
                    CustomDatePickerTextFieldComponent(
                        isMandatory = true,
                        defaultValue = viewModel.selectedDate.value,
                        title = viewModel.translationHelper.stringResource(context, R.string.date),
                        isEditable = true,
                        hintText = viewModel.translationHelper.stringResource(
                            context,
                            R.string.select
                        )
                            ?: BLANK_STRING,
                        datePickerState = datePickerState,
                        datePickerProperties = datePickerProperties,
                        datePickerDialogProperties = datePickerDialogProperties,
                        onDateSelected = { date ->
                            viewModel.selectedDate.value = date.value().getDate()
                            viewModel.selectedDateInLong = date.value()
                            viewModel.validateForm(
                                subjectId = subjectId,
                                fieldName = AddEventFieldEnum.DATE.name,
                                transactionId = transactionId,
                            ) { isValid, message ->

                            }
                        }
                    )
                }

                item {
                    TypeDropDownComponent(
                        isEditAllowed = !showDeleteButton,
                        title = viewModel.translationHelper.stringResource(
                            context,
                            R.string.livelihood
                        ),
                        isMandatory = true,
                        sources = viewModel.livelihoodDropdownValue,
                        selectedValue = viewModel.livelihoodDropdownValue.find { it.id == viewModel.selectedLivelihoodId.value }?.value,

                        onAnswerSelection = { selectedValue ->
                            viewModel.onLivelihoodSelect(selectedValue.id, subjectId, transactionId)
                            viewModel.validateForm(
                                subjectId = subjectId,
                                fieldName = AddEventFieldEnum.LIVELIHOOD_TYPE.name,
                                transactionId = transactionId,

                                ) { isValid, message ->

                            }
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
                        title = viewModel.translationHelper.stringResource(
                            context,
                            R.string.events
                        ),
                        isMandatory = true,
                        selectedValue = viewModel.livelihoodEventDropdownValue.find { it.id == viewModel.selectedEventId.value }?.value,
                        sources = viewModel.livelihoodEventDropdownValue,
                        onAnswerSelection = { selectedValue ->
                            viewModel.onEventSelected(selectedValue, subjectId)
                            viewModel.validateForm(
                                subjectId = subjectId,
                                fieldName = AddEventFieldEnum.EVENT_TYPE.name,
                                transactionId = transactionId
                            ) { isValid, message ->
                                if (isValid) {
                                    viewModel.loadAssetAndProduct()
                                }
                                viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.EVENT_TYPE.name] =
                                    Pair(isValid, message)

                            }
                        })
                    if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.EVENT_TYPE.name]?.second)) {

                        Text(
                            text = viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.EVENT_TYPE.name]?.second
                                ?: BLANK_STRING,
                            modifier = Modifier.padding(horizontal = dimen_5_dp),
                            style = quesOptionTextStyle.copy(color = eventTextColor)
                        )

                    }
                }




                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET].value()) {
                    item {

                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.translationHelper.stringResource(
                                context,
                                R.string.type_of_asset
                            ),
                            selectedValue = viewModel.livelihoodAssetDropdownValue.find { it.id == viewModel.selectedAssetTypeId.value }?.value,
                            isMandatory = true,
                            sources = viewModel.livelihoodAssetDropdownValue,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedAssetTypeId.value = selectedValue.id
                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.ASSET_TYPE.name,
                                    transactionId = transactionId
                                ) { isValid, message ->

                                    viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_TYPE.name] =
                                        Pair(isValid, message)
                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(color = eventTextColor)
                            )
                        }
                    }


                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT].value()) {
                    item {
                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.translationHelper.stringResource(
                                context,
                                R.string.products
                            ),
                            isMandatory = true,
                            sources = viewModel.livelihoodProductDropdownValue,
                            selectedValue = viewModel.livelihoodProductDropdownValue.find { it.id == viewModel.selectedProductId.value }?.value,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedProductId.value = selectedValue.id
                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.PRODUCT_TYPE.name,
                                    transactionId = transactionId,
                                ) { isValid, message ->

                                    viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.PRODUCT_TYPE.name] =
                                        Pair(isValid, message)
                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.PRODUCT_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.PRODUCT_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(color = eventTextColor)
                            )
                        }

                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.COUNT_OF_ASSET].value()) {
                    item {
                        val str =
                            if (getLivelihoodEventFromName(viewModel.eventType).assetJournalEntryFlowType?.name?.equals(
                                    INFLOW
                                ) == true
                            ) viewModel.translationHelper.stringResource(
                                context,
                                R.string.increase_in_number
                            ) else viewModel.translationHelper.stringResource(
                                context,
                                R.string.decrease_in_number
                            )
                        IncrementDecrementNumberComponent(
                            isMandatory = true,
                            title = str,
                            isEditAllowed = true,
                            currentValue = viewModel.assetCount.value,
                            maxValue = viewModel.maxAssetValue.value,
                            onAnswerSelection = { inputValue ->
                                viewModel.assetCount.value = inputValue
                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    AddEventFieldEnum.ASSET_COUNT.name,
                                    transactionId = transactionId
                                ) { isValid, message ->
                                    viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_COUNT.name] =
                                        Pair(isValid, message)

                                }
                                viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_COUNT.name]?.first
                                    ?: false
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_COUNT.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.ASSET_COUNT.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(color = eventTextColor)
                            )
                        }
                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.AMOUNT].value()) {
                    item {
                        InputComponent(
                            maxLength = 7,
                            isMandatory = true,
                            isEditable = true,
                            defaultValue = viewModel.amount.value,
                            title = viewModel.translationHelper.stringResource(
                                context,
                                R.string.amount
                            ),
                            isOnlyNumber = true,
                            hintText = BLANK_STRING
                        ) { selectedValue, remainingAmout ->
                            viewModel.amount.value = selectedValue

                            viewModel.validateForm(
                                subjectId = subjectId,
                                fieldName = AddEventFieldEnum.AMOUNT.name,
                                transactionId = transactionId,
                            ) { isValid, message ->
                                viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.AMOUNT.name] =
                                    Pair(isValid, message)

                            }
                        }
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.AMOUNT.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap[AddEventFieldEnum.AMOUNT.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(color = eventTextColor)
                            )
                        }
                    }

                }
                customVerticalSpacer(size = 100.dp)

            }

            if (viewModel.showDeleteDialog.value) {
                ShowCustomDialog(
                    message = viewModel.translationHelper.stringResource(
                        context,
                        R.string.are_you_sure_you_want_to_delete
                    ),
                    negativeButtonTitle = viewModel.translationHelper.stringResource(
                        context,
                        R.string.no
                    ),
                    positiveButtonTitle = viewModel.translationHelper.stringResource(
                        context,
                        R.string.yes
                    ),
                    onNegativeButtonClick = {
                        viewModel.showDeleteDialog.value = false
                    },
                    onPositiveButtonClick = {
                        viewModel.onDeleteClick(transactionId, subjectId)
                        viewModel.showDeleteDialog.value = false
                        popBackToPreviousScreen(
                            navController,
                            viewModel,
                            message = context.getString(R.string.event_deleted_successfully),
                            transactionId
                        )


                    }
                )
            }
        }
    )
}

private fun popBackToPreviousScreen(
    navController: NavHostController,
    viewModel: AddEventViewModel,
    message: String,
    transactionId: String
) {
    navController.previousBackStackEntry?.savedStateHandle?.set(
        EVENT_MESSAGE,
        message
    )
    navController.previousBackStackEntry?.savedStateHandle?.set(
        SELECTED_LIVELIHOOD_ID,
        viewModel.selectedLivelihoodId.value
    )
    navController.previousBackStackEntry?.savedStateHandle?.set(
        NEWLY_ADDED_EVENT_TRANSACTION_ID, transactionId
    )
    navController.popBackStack()
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddEventScreenPreview() {
    AddEventScreen(subjectId = 0, subjectName = "", transactionId = "", onSettingClick = {})
}