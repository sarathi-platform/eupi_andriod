package com.nudge.incomeexpensemodule.ui.edit_history_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.incomeexpensemodule.R
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.ui.commonUi.CustomDateRangePickerBottomSheetComponent
import com.nudge.core.ui.commonUi.CustomDateRangePickerDisplay
import com.nudge.core.ui.commonUi.SheetHeight
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomDateRangePickerSheetState
import com.nudge.core.ui.commonUi.rememberDateRangePickerBottomSheetProperties
import com.nudge.core.ui.commonUi.rememberDateRangePickerProperties
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_15_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.searchFieldBg
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditHistoryScreen(
    navController: NavHostController,
    viewModel: EditHistoryScreenViewModel, transactionId: String,
    onSettingClick: () -> Unit

) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitEditHistoryState(transactionId = transactionId))
    }
    val sheetState = rememberCustomDateRangePickerSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val sheetProperties = rememberDateRangePickerBottomSheetProperties(
        sheetState = sheetState,
        modifier = Modifier,
        sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
        sheetBackgroundColor = searchFieldBg,
    )

    val dateRangePickerProperties = rememberDateRangePickerProperties(
        dateValidator = {
            it <= getCurrentTimeInMillis()
        }
    )

    val scope = rememberCoroutineScope()
    CustomDateRangePickerBottomSheetComponent(
        customDateRangePickerBottomSheetProperties = sheetProperties,
        dateRangePickerProperties = dateRangePickerProperties,
        sheetHeight = SheetHeight.CustomSheetHeight(dimen_56_dp),
        onSheetConfirmButtonClicked = {

            if (dateRangePickerProperties.state.selectedEndDateMillis == null) {
                dateRangePickerProperties.state.setSelection(
                    dateRangePickerProperties.state.selectedStartDateMillis,
                    getCurrentTimeInMillis()
                )
            }

            viewModel.onEvent(
                CommonEvents.UpdateDateRange(
                    dateRangePickerProperties.state.selectedStartDateMillis,
                    dateRangePickerProperties.state.selectedEndDateMillis
                )
            )

            scope.launch {
                sheetState.hide()
            }
        }
    ) {
        ToolBarWithMenuComponent(
            title = "Edit history",
            modifier = Modifier,
            onBackIconClick = { navController.popBackStack() },
            onSearchValueChange = {},
            onBottomUI = { },
            onContentUI = { a, b, c ->
                if (viewModel.loaderState.value.isLoaderVisible) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(dimen_24_dp))
                    }
                } else {
                    CustomDateRangePickerDisplay(
                        modifier = Modifier.padding(horizontal = dimen_15_dp),
                        value = "${viewModel.dateRangeFilter.value.first.getDate()} - ${viewModel.dateRangeFilter.value.second.getDate()}",
                        label = stringResource(R.string.date_range_picker_label_text)
                    ) {
                        scope.launch {
                            sheetState.show()
                        }
                    }
                    val isDeletedAllData =
                        !viewModel.subjectLivelihoodEventSummaryUiModelList.any { it.status == 1 }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimen_10_dp), modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = dimen_10_dp)
                    ) {
                        customVerticalSpacer()
                        val items = viewModel.subjectLivelihoodEventSummaryUiModelList
                        items(items.size) { index ->
                            val currentItem = items[index]
                            val nextItem = if (index < items.size - 1) items[index + 1] else null
                            EditHistoryRow(
                                currentHistoryData = currentItem,
                                nextHistoryData = nextItem,
                                isRecentData = index == 0,
                                isDeleted = isDeletedAllData
                            )
                        }
                        customVerticalSpacer()
                    }
                }

            },
            onRetry = {},
            onSettingClick = { onSettingClick() })
    }

}