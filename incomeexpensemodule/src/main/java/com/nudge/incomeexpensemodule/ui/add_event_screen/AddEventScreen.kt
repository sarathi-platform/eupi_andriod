package com.nudge.incomeexpensemodule.ui.add_event_screen

import android.app.Activity
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.nudge.core.setKeyboardToPan
import com.nudge.core.setKeyboardToReadjust
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
import com.nudge.core.ui.theme.redIconColor
import com.nudge.core.ui.theme.redOffline
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.showDeleteButton.value = showDeleteButton
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
    DisposableEffect(Unit) {
        onDispose {
            setKeyboardToPan((context as Activity))
        }
    }
    setKeyboardToReadjust((context as Activity))

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
        title = if (showDeleteButton) viewModel.stringResource(
            R.string.edit_event
        ) else viewModel.stringResource(R.string.add_event),
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
                            buttonTitle = viewModel.getString(
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
                        buttonTitle = viewModel.getString(
                            R.string.save_text,
                        ),
                        isActive = viewModel.isSubmitButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            viewModel.isSubmitButtonEnable.value = false
                            viewModel.onSubmitButtonClick(subjectId, transactionId) {
                                popBackToPreviousScreen(
                                    navController,
                                    viewModel,
                                    message = viewModel.getString(
                                        R.string.event_added_successfully
                                    ),
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
                    TypeDropDownComponent(
                        isEditAllowed = !showDeleteButton,
                        title = viewModel.stringResource(
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
                }



                item {

                    TypeDropDownComponent(
                        isEditAllowed = !showDeleteButton,
                        title = viewModel.stringResource(
                            R.string.events
                        ),
                        isMandatory = true,
                        selectedValue = viewModel.livelihoodEventDropdownValue.find { it.id == viewModel.selectedEventId.value }?.value,
                        sources = viewModel.livelihoodEventDropdownValue,
                        isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.EVENT_TYPE.name]?.first == false,
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
                                viewModel.updateFieldValidationMessageAndMap(
                                    key = AddEventFieldEnum.EVENT_TYPE.name,
                                    value = Pair(isValid, message)
                                )

                            }
                        })
                    if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.EVENT_TYPE.name]?.second)) {

                        Text(
                            text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.EVENT_TYPE.name]?.second
                                ?: BLANK_STRING,
                            modifier = Modifier.padding(horizontal = dimen_5_dp),
                            style = quesOptionTextStyle.copy(
                                color =
                                if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.EVENT_TYPE.name]?.first == false)
                                    redOffline else eventTextColor
                            )
                        )

                    }
                }



                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET].value()) {
                    item {

                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.stringResource(
                                R.string.type_of_asset
                            ),
                            selectedValue = viewModel.livelihoodAssetDropdownValue.find { it.id == viewModel.selectedAssetTypeId.value }?.value,
                            isMandatory = true,
                            sources = viewModel.livelihoodAssetDropdownValue,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_TYPE.name]?.first == false,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedAssetTypeId.value = selectedValue.id
                                resetAmountAssetType(viewModel)

                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.ASSET_TYPE.name,
                                    transactionId = transactionId
                                ) { isValid, message ->
                                    viewModel.updateAssetVisibility(isValid)
                                    viewModel.updateFieldValidationMessageAndMap(
                                        key = AddEventFieldEnum.ASSET_TYPE.name,
                                        value = Pair(isValid, message)
                                    )

                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(
                                    color =
                                    if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_TYPE.name]?.first == false)
                                        redOffline else eventTextColor
                                )
                            )
                        }
                    }


                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT].value()) {
                    item {
                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.stringResource(
                                R.string.products
                            ),
                            isMandatory = true,
                            sources = viewModel.livelihoodProductDropdownValue,
                            selectedValue = viewModel.livelihoodProductDropdownValue.find { it.id == viewModel.selectedProductId.value }?.value,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.PRODUCT_TYPE.name]?.first == false,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedProductId.value = selectedValue.id
                                resetAmountAssetType(viewModel)
                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.PRODUCT_TYPE.name,
                                    transactionId = transactionId,
                                ) { isValid, message ->

                                    viewModel.updateFieldValidationMessageAndMap(
                                        key = AddEventFieldEnum.PRODUCT_TYPE.name,
                                        Pair(isValid, message)
                                    )

                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.PRODUCT_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.PRODUCT_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(
                                    color =
                                    if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.PRODUCT_TYPE.name]?.first == false)
                                        redOffline else eventTextColor
                                )
                            )
                        }

                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_CHILD_ASSET].value()) {
                    item {

                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.stringResource(
                                R.string.type_of_child_asset
                            ),
                            selectedValue = viewModel.livelihoodChildAssetDropdownValue.find { it.id == viewModel.selectedChildAssetTypeId.value }?.value,
                            isMandatory = true,
                            sources = viewModel.livelihoodChildAssetDropdownValue,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.CHILD_ASSET_TYPE.name]?.first == false,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedChildAssetTypeId.value = selectedValue.id
                                viewModel.selectedAssetTypeId.value = -1
                                resetAmountAssetType(viewModel)

                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.CHILD_ASSET_TYPE.name,
                                    transactionId = transactionId,
                                    isChildTransition = true
                                ) { isValid, message ->
                                    viewModel.updateAssetVisibility(isValid)
                                    viewModel.updateFieldValidationMessageAndMap(
                                        key = AddEventFieldEnum.CHILD_ASSET_TYPE.name,
                                        value = Pair(isValid, message)
                                    )

                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.CHILD_ASSET_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.CHILD_ASSET_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(
                                    color =
                                    if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.CHILD_ASSET_TYPE.name]?.first == false)
                                        redOffline else eventTextColor
                                )
                            )
                        }
                    }


                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ADULT_ASSET].value()) {
                    item {

                        TypeDropDownComponent(
                            isEditAllowed = true,
                            title = viewModel.stringResource(
                                R.string.type_of_adult_asset
                            ),
                            selectedValue = viewModel.livelihoodAssetDropdownValue.find { it.id == viewModel.selectedAssetTypeId.value }?.value,
                            isMandatory = true,
                            sources = viewModel.livelihoodAssetDropdownValue,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ADULT_ASSET_TYPE.name]?.first == false,
                            onAnswerSelection = { selectedValue ->
                                viewModel.selectedAssetTypeId.value = selectedValue.id
                                resetAmountAssetType(viewModel)

                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    fieldName = AddEventFieldEnum.ADULT_ASSET_TYPE.name,
                                    transactionId = transactionId
                                ) { isValid, message ->
                                    viewModel.updateAssetVisibility(isValid)
                                    viewModel.updateFieldValidationMessageAndMap(
                                        key = AddEventFieldEnum.ADULT_ASSET_TYPE.name,
                                        value = Pair(isValid, message)
                                    )

                                }
                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ADULT_ASSET_TYPE.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ADULT_ASSET_TYPE.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(
                                    color =
                                    if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ADULT_ASSET_TYPE.name]?.first == false)
                                        redOffline else eventTextColor
                                )
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
                            ) viewModel.stringResource(
                                R.string.increase_in_number
                            ) else viewModel.stringResource(
                                R.string.decrease_in_number
                            )
                        IncrementDecrementNumberComponent(
                            isMandatory = true,
                            title = str,
                            isEditAllowed = true,
                            currentValue = viewModel.assetCount.value,
                            maxValue = viewModel.maxAssetValue.value,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_COUNT.name]?.first == false,
                            onAnswerSelection = { inputValue ->
                                viewModel.assetCount.value = inputValue
                                viewModel.validateForm(
                                    subjectId = subjectId,
                                    AddEventFieldEnum.ASSET_COUNT.name,
                                    transactionId = transactionId
                                ) { isValid, message ->
                                    viewModel.updateFieldValidationMessageAndMap(
                                        key = AddEventFieldEnum.ASSET_COUNT.name,
                                        value = Pair(isValid, message)
                                    )

                                }

                            }
                        )
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_COUNT.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_COUNT.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                style = quesOptionTextStyle.copy(
                                    color =
                                    if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.ASSET_COUNT.name]?.first == false)
                                        redOffline else eventTextColor
                                )
                            )
                        }
                    }

                }
                if (viewModel.questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.AMOUNT].value()) {
                    item {
                        InputComponent(
                            maxLength = 5,
                            isMandatory = true,
                            isEditable = true,
                            defaultValue = viewModel.amount.value,
                            title = viewModel.stringResource(
                                R.string.amount
                            ),
                            isOnlyNumber = true,
                            hintText = BLANK_STRING,
                            isError = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.AMOUNT.name]?.first == false && !TextUtils.isEmpty(
                                viewModel.amount.value
                            ),

                        ) { selectedValue, remainingAmout ->
                            viewModel.amount.value = selectedValue

                            viewModel.validateForm(
                                subjectId = subjectId,
                                fieldName = AddEventFieldEnum.AMOUNT.name,
                                transactionId = transactionId,
                            ) { isValid, message ->
                                viewModel.updateFieldValidationMessageAndMap(
                                    key = AddEventFieldEnum.AMOUNT.name,
                                    value = Pair(isValid, message)
                                )

                            }
                        }
                        if (!TextUtils.isEmpty(viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.AMOUNT.name]?.second)) {
                            Text(
                                text = viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.AMOUNT.name]?.second
                                    ?: BLANK_STRING,
                                modifier = Modifier.padding(horizontal = dimen_5_dp),
                                color = if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.AMOUNT.name]?.first == true) eventTextColor else redIconColor,
                                style = quesOptionTextStyle.copy(color = if (viewModel.fieldValidationAndMessageMap.collectAsState().value[AddEventFieldEnum.AMOUNT.name]?.first == true) eventTextColor else redIconColor)
                            )
                        }
                    }

                }
                if (viewModel.isDateOfEventVisible.value) {
                    item {
                        CustomDatePickerTextFieldComponent(
                            isMandatory = true,
                            defaultValue = viewModel.selectedDate.value,
                            title = viewModel.stringResource(R.string.date_of_event),
                            isEditable = true,
                            hintText = viewModel.stringResource(
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
                }
                customVerticalSpacer(size = 100.dp)

            }

            if (viewModel.showDeleteDialog.value) {
                ShowCustomDialog(
                    message = viewModel.stringResource(
                        R.string.are_you_sure_you_want_to_delete
                    ),
                    negativeButtonTitle = viewModel.stringResource(
                        R.string.no
                    ),
                    positiveButtonTitle = viewModel.stringResource(
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

private fun resetAmountAssetType(viewModel: AddEventViewModel) {
    viewModel.assetCount.value = BLANK_STRING
    viewModel.amount.value = BLANK_STRING
    viewModel.updateFieldValidationMessageAndMap(
        key = AddEventFieldEnum.ASSET_COUNT.name,
        value = Pair(true, BLANK_STRING)
    )

    viewModel.updateFieldValidationMessageAndMap(
        key = AddEventFieldEnum.AMOUNT.name,
        value = Pair(true, BLANK_STRING)
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