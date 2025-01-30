package com.nudge.incomeexpensemodule.ui.data_summary_screen

import android.text.TextUtils
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.incomeexpensemodule.R
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_mmm_YY_FORMAT
import com.nudge.core.DEFAULT_ID
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.ui.commonUi.CustomDateRangePickerBottomSheetComponent
import com.nudge.core.ui.commonUi.CustomDateRangePickerDisplay
import com.nudge.core.ui.commonUi.CustomSubTabLayoutWithCallBack
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.MeasureUnconstrainedViewWidthComponent
import com.nudge.core.ui.commonUi.SheetHeight
import com.nudge.core.ui.commonUi.StrikethroughText
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.rememberCustomDateRangePickerSheetState
import com.nudge.core.ui.commonUi.rememberDateRangePickerBottomSheetProperties
import com.nudge.core.ui.commonUi.rememberDateRangePickerProperties
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGreen
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.didiDetailItemStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_15_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_40_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.h6Bold
import com.nudge.core.ui.theme.incomeCardBorderColor
import com.nudge.core.ui.theme.lightGreen
import com.nudge.core.ui.theme.newBoldTextStyle
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.searchFieldBg
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.stepIconDisableColor
import com.nudge.core.ui.theme.taskCompletionBannerBgColor
import com.nudge.core.ui.theme.white
import com.nudge.core.ui.theme.yellowBg
import com.nudge.core.value
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
import com.nudge.incomeexpensemodule.navigation.navigateToAddEventScreen
import com.nudge.incomeexpensemodule.navigation.navigateToEditHistoryScreen
import com.nudge.incomeexpensemodule.ui.AssetsDialog
import com.nudge.incomeexpensemodule.ui.component.SingleSelectDropDown
import com.nudge.incomeexpensemodule.ui.component.TotalIncomeExpenseAssetSummaryView
import com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel.DataSummaryScreenViewModel
import com.nudge.incomeexpensemodule.utils.EVENT_MESSAGE
import com.nudge.incomeexpensemodule.utils.NEWLY_ADDED_EVENT_TRANSACTION_ID
import com.nudge.incomeexpensemodule.utils.SELECTED_LIVELIHOOD_ID
import com.nudge.incomeexpensemodule.utils.findById
import com.nudge.incomeexpensemodule.utils.getTextColor
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.find
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DataSummaryScreen(
    navController: NavHostController,
    viewModel: DataSummaryScreenViewModel,
    subjectId: Int,
    subjectName: String,
    onSettingClick: () -> Unit,
) {
    val stateHandle = navController.currentBackStackEntry?.savedStateHandle
    //Listen the result from Add event screen
    val eventMessage = remember { stateHandle?.getLiveData<String>(EVENT_MESSAGE) }
    val selectedLivelihoodId = remember { stateHandle?.getLiveData<Int>(SELECTED_LIVELIHOOD_ID) }
    val newlyAddedEvent =
        remember { stateHandle?.getLiveData<String>(NEWLY_ADDED_EVENT_TRANSACTION_ID) }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.setPreviousScreenData(
            subjectId,
            selectedLivelihoodId?.value ?: 0
        )
        viewModel.onEvent(InitDataEvent.InitDataSummaryScreenState(subjectId = subjectId))

    }
    DisposableEffect(key1 = LocalContext.current) {
        onDispose {
            eventMessage?.value = BLANK_STRING
            newlyAddedEvent?.value = BLANK_STRING
        }
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

    if (viewModel.showAssetDialog.value) {
        AssetsDialog(
            viewModel.getLivelihood(),
            viewModel.livelihoodModel,
            onDismissRequest = {
                viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )
    }

    val showMoreItems = remember {
        mutableStateOf(false)
    }

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

            viewModel.onEvent(
                DataSummaryScreenEvents.CustomDateRangeFilterSelected(viewModel.tabs.map { it.id }
                    .indexOf(SubTabs.CustomDateRange.id))
            )

            scope.launch {
                sheetState.hide()
            }
        }
    ) {
        ToolBarWithMenuComponent(
            title = subjectName,
            modifier = Modifier.fillMaxSize(),
            onBackIconClick = {
                navController.navigateUp()
            },
            onSearchValueChange = {},
            onBottomUI = {
                if (viewModel.areEventsNotAvailableForSubject.value) {
                    BottomAppBar(
                        backgroundColor = Color.White,
                        elevation = 10.dp
                    ) {
                        AddEventButton(viewModel = viewModel) {
                            navigateToAddEventScreen(
                                navController = navController,
                                subjectName = subjectName,
                                subjectId = subjectId,
                                transactionID = UUID.randomUUID().toString(),
                                showDeleteButton = false
                            )
                        }

                    }
                }
            },
            onContentUI = { a, b, c ->
                if (!TextUtils.isEmpty(eventMessage?.value)) {
                    TaskCompletionMessageDemo(eventMessage?.value ?: BLANK_STRING)
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    if (!viewModel.areEventsNotAvailableForSubject.value) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AddEventButton(viewModel = viewModel) {
                                eventMessage?.value = BLANK_STRING
                                selectedLivelihoodId?.value = 0
                                navigateToAddEventScreen(
                                    navController = navController,
                                    subjectName = subjectName,
                                    subjectId = subjectId,
                                    transactionID = UUID.randomUUID().toString(),
                                    showDeleteButton = false
                                )
                            }
                        }
                    } else {
                        if (viewModel.loaderState.value.isLoaderVisible) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(dimen_24_dp))
                            }
                        } else {
                            DataSummaryView(
                                viewModel,
                                showMoreItems = showMoreItems.value,
                                onEventItemClicked = { transactionId ->
                                    navigateToAddEventScreen(
                                        navController = navController,
                                        subjectName = subjectName,
                                        subjectId = subjectId,
                                        transactionID = transactionId,
                                        showDeleteButton = true
                                    )
                                },
                                dateRangePickerClicked = {
                                    scope.launch {
                                        sheetState.show()
                                    }
                                },
                                onShowModeClicked = {
                                    showMoreItems.value = !showMoreItems.value
                                },
                                onViewEditItemClicked = { transactionId ->
                                    navigateToEditHistoryScreen(
                                        navController = navController,
                                        transactionID = transactionId
                                    )
                                },
                                newlyAddedEvent = newlyAddedEvent?.value ?: BLANK_STRING
                            )
                        }
                    }

                }

            },
            onSettingClick = { onSettingClick() }) {

        }
    }
}

@Composable
private fun DataSummaryView(
    viewModel: DataSummaryScreenViewModel,
    showMoreItems: Boolean,
    onEventItemClicked: (transactionId: String) -> Unit,
    dateRangePickerClicked: () -> Unit,
    onViewEditItemClicked: (transactionId: String) -> Unit,
    onShowModeClicked: () -> Unit,
    newlyAddedEvent: String
) {
    val context = LocalContext.current
    TabBarContainer(viewModel.tabs) {
        if (TabsCore.getSubTabForTabIndex(TabsEnum.DataSummaryTab.tabIndex) == viewModel.tabs.map { it.id }
                .indexOf(SubTabs.CustomDateRange.id)) {
            viewModel.showCustomDatePicker.value = true
        } else {
            viewModel.showCustomDatePicker.value = false
            viewModel.onEvent(
                DataSummaryScreenEvents.TabFilterSelected(
                    TabsCore.getSubTabForTabIndex(
                        TabsEnum.DataSummaryTab.tabIndex
                    )
                )
            )
        }

    }
    Spacer(modifier = Modifier.height(16.dp))

    if (viewModel.showCustomDatePicker.value) {
        CustomDateRangePickerDisplay(
            value = "${viewModel.dateRangeFilter.value.first.getDate()} - ${viewModel.dateRangeFilter.value.second.getDate()}",
            label = viewModel.stringResource(
                R.string.date_range_picker_label_text
            )
        ) {
            dateRangePickerClicked()
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    Text(
        stringResource(R.string.livelihood),
        style = didiDetailItemStyle.copy(color = stepIconDisableColor)
    )
    Spacer(modifier = Modifier.height(dimen_5_dp))
    DropDownContainer(viewModel.livelihoodDropdownList.toList()) {
        viewModel.onEvent(DataSummaryScreenEvents.FilterDataForLivelihood(it))
    }
    Spacer(modifier = Modifier.height(16.dp))
    HeaderSection(viewModel.getLivelihood()) {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(true))
    }
    Spacer(modifier = Modifier.height(16.dp))
    EventsListHeaderWithDropDownFilter(
        viewModel = viewModel,
        viewModel.eventsSubFilterList,
        selectedValue = viewModel.selectedEventsSubFilter.value,
        showMoreItems
    ) { selectedFilterId ->
        viewModel.onEvent(DataSummaryScreenEvents.EventsSubFilterSelected(selectedFilterId))
    }

    Spacer(modifier = Modifier.height(16.dp))
    EventView(
        viewModel = viewModel,
        viewModel.filteredSubjectLivelihoodEventSummaryUiModelList.toList()
            .sortedByDescending { it.date },
        eventsList = viewModel.getEventsList(),
        showMoreItems = showMoreItems,
        onEventItemClicked = onEventItemClicked,
        onViewEditItemClicked = onViewEditItemClicked,
        onShowModeClicked = {
            onShowModeClicked()
        },
        newlyAddedEvent = newlyAddedEvent
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun TabBarContainer(tabs: List<SubTabs>, onClick: () -> Unit) {

    TabsCore.setTabIndex(TabsEnum.DataSummaryTab.tabIndex)
    CustomSubTabLayoutWithCallBack(
        parentTabIndex = TabsEnum.DataSummaryTab.tabIndex,
        tabs = tabs
    ) {
        onClick()
    }
}

@Composable
fun DropDownContainer(livelihoodList: List<ValuesDto>, onValueSelected: (id: Int) -> Unit) {
    SingleSelectDropDown(
        sources = livelihoodList,
        selectOptionText = livelihoodList.find { it.isSelected == true }?.id
            ?: if (livelihoodList.isNotEmpty()) livelihoodList.first().id else DEFAULT_ID
    ) {
        onValueSelected(it)
    }

}

@Composable
fun HeaderSection(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    onAssetCountClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = incomeCardBorderColor,
                shape = RoundedCornerShape(roundedCornerRadiusDefault)
            )
            .background(yellowBg, shape = RoundedCornerShape(roundedCornerRadiusDefault))
            .padding(vertical = dimen_10_dp, horizontal = dimen_14_dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TotalIncomeExpenseAssetSummaryView(incomeExpenseSummaryUiModel, onAssetCountClicked)
    }

}

@Composable
fun EventsListHeaderWithDropDownFilter(
    viewModel: DataSummaryScreenViewModel,
    eventSubFilterSelected: List<ValuesDto>,
    selectedValue: Int,
    showMoreItems: Boolean,
    onEventSubFilterSelected: (selectedFilterId: Int) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sources =
            eventSubFilterSelected

        var selectedOptionValue by remember {
            mutableStateOf(sources.findById(selectedValue) ?: sources[0])
        }


        Text(
            if (showMoreItems) viewModel.stringResource(
                R.string.all_events
            ) else viewModel.stringResource(
                R.string.last_events,
                DEFAULT_EVENT_LIST_VIEW_SIZE
            ),
            style = getTextColor(defaultTextStyle)
        )
        MeasureUnconstrainedViewWidthComponent(viewToMeasure = { Text(text = selectedOptionValue.value) }) {
            SingleSelectDropDown(
                sources = sources,
                selectOptionText = selectedOptionValue.id,
                modifier = Modifier.width(it + dimen_60_dp + dimen_16_dp),
                height = dimen_40_dp
            ) { selectValue ->

                selectedOptionValue = sources.findById(selectValue) ?: sources[0]
                onEventSubFilterSelected(selectValue)
            }
        }

    }
}


@Composable
fun ShowMoreButton(
    viewModel: DataSummaryScreenViewModel,
    showMoreItems: Boolean,
    onShowModeClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}, horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                onShowModeClicked()
            },
            modifier = Modifier
                .height(48.dp)
                .border(
                    width = 1.dp, color = borderGreyLight, shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (showMoreItems) viewModel.stringResource(
                        R.string.show_less
                    ) else viewModel.stringResource(
                        R.string.show_more
                    ),
                    textAlign = TextAlign.Center,
                    style = getTextColor(defaultTextStyle),
                )
                Icon(
                    imageVector = if (showMoreItems) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = assetValueIconColor
                )
            }
        }
    }

}


@Composable
private fun EventView(
    viewModel: DataSummaryScreenViewModel,
    filteredSubjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventSummaryUiModel>,
    eventsList: List<LivelihoodEventUiModel>?,
    showMoreItems: Boolean,
    onEventItemClicked: (transactionId: String) -> Unit,
    onViewEditItemClicked: (transactionId: String) -> Unit,
    onShowModeClicked: () -> Unit,
    newlyAddedEvent: String
) {
    val backgroundcolor = remember { Animatable(Color.White) }
    val bordercolor = remember { Animatable(Color.White) }
    LaunchedEffect(Unit) {
        launch {
            bordercolor.animateTo(borderGreen, animationSpec = tween(2000))
            bordercolor.animateTo(Color.White, animationSpec = tween(2000))
        }
        launch {
            backgroundcolor.animateTo(lightGreen, animationSpec = tween(2000))
            backgroundcolor.animateTo(Color.White, animationSpec = tween(2000))
        }

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        itemsIndexed(
            filteredSubjectLivelihoodEventSummaryUiModelList.toList()
                .take(DEFAULT_EVENT_LIST_VIEW_SIZE)
        ) { index, subjectLivelihoodEventSummaryUiModel ->
            val highlightedBackgroundColor =
                if (newlyAddedEvent == subjectLivelihoodEventSummaryUiModel.transactionId) backgroundcolor.value else white
            val highlightedBorderColor =
                if (newlyAddedEvent == subjectLivelihoodEventSummaryUiModel.transactionId) bordercolor.value else white
            Box(
                Modifier
                    .background(
                        color = highlightedBackgroundColor,
                        shape = RoundedCornerShape(dimen_6_dp)
                    )
                    .border(
                        width = dimen_1_dp,
                        color = highlightedBorderColor,
                        shape = RoundedCornerShape(dimen_6_dp)
                    )
                    .padding(
                        top = dimen_5_dp,
                        start = dimen_8_dp,
                        end = dimen_8_dp,
                        bottom = dimen_3_dp
                    )
            ) {
                Column {
                    EventHeader(
                        viewModel = viewModel,
                        subjectLivelihoodEventSummaryUiModel,
                        eventsList
                    )
                    EventDetails(viewModel = viewModel,subjectLivelihoodEventSummaryUiModel) {
                        if (subjectLivelihoodEventSummaryUiModel.status != 2) {
                            onEventItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                        }
                    }
                    ViewEditHistoryView(
                        isEventDeleted = subjectLivelihoodEventSummaryUiModel.isEventNotActive(),
                        onClick = {
                            onViewEditItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                        })
                    CustomVerticalSpacer(size = dimen_8_dp)
                    if (filteredSubjectLivelihoodEventSummaryUiModelList.size != 1) {
                        Divider(thickness = dimen_1_dp, color = borderGreyLight)
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = showMoreItems,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    filteredSubjectLivelihoodEventSummaryUiModelList.toList().drop(
                        DEFAULT_EVENT_LIST_VIEW_SIZE
                    ).forEachIndexed { index, subjectLivelihoodEventSummaryUiModel ->
                        Box(
                            Modifier
                                .background(color = white)
                                .border(width = dimen_1_dp, color = white)
                                .padding(
                                    top = dimen_5_dp,
                                    start = dimen_8_dp,
                                    end = dimen_8_dp,
                                    bottom = dimen_3_dp
                                )
                        ) {
                        Column {
                            EventHeader(
                                viewModel = viewModel,
                                subjectLivelihoodEventSummaryUiModel,
                                eventsList
                            )
                            EventDetails(
                                viewModel = viewModel,
                                subjectLivelihoodEventSummaryUiModel,
                            ) {
                                onEventItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                            }
                            ViewEditHistoryView(
                                isEventDeleted = subjectLivelihoodEventSummaryUiModel.isEventNotActive(),
                                onClick = {
                                    onViewEditItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                                })
                            CustomVerticalSpacer(size = dimen_8_dp)
                            Divider(thickness = dimen_1_dp, color = borderGreyLight)

                        }
                        }


                }
                }
            }
        }

        if (filteredSubjectLivelihoodEventSummaryUiModelList.size > DEFAULT_EVENT_LIST_VIEW_SIZE) {
            item {
                CustomVerticalSpacer(size = dimen_5_dp)
                ShowMoreButton(viewModel = viewModel, showMoreItems) {

                    onShowModeClicked()
                }
            }
        }
        item {
            CustomVerticalSpacer(size = dimen_60_dp)
        }

    }
}

@Composable
private fun ViewEditHistoryView(onClick: () -> Unit, isEventDeleted: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        Text(
            modifier = Modifier.clickable { onClick() },
            text = stringResource(R.string.view_edit_history),
            style = newMediumTextStyle.copy(assetValueIconColor)
        )
        if (isEventDeleted) {
            Spacer(modifier = Modifier.weight(1.0f))
            Image(
                painter = painterResource(id = R.drawable.ic_delete_stamp),
                contentDescription = null,
            )

        }
    }
}

const val DEFAULT_EVENT_LIST_VIEW_SIZE = 3

@Composable
private fun EventHeader(
    viewModel: DataSummaryScreenViewModel,
    item: SubjectLivelihoodEventSummaryUiModel,
    livelihoodEventUiModels: List<LivelihoodEventUiModel>?
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = dimen_5_dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            TextWithPaddingEnd(
                text = viewModel.stringResource(R.string.event),
                style = getTextColor(smallTextStyle, color = eventTextColor)
            )
            StrikethroughText(
                text = livelihoodEventUiModels.find(item.livelihoodEventId.value())?.name.value(),
                textStyle = getTextColor(newBoldTextStyle),
                isStrikethrough = item.isEventNotActive()
            )
        }
        StrikethroughText(
            text = item.date.getDate(pattern = DD_mmm_YY_FORMAT),
            textStyle = getTextColor(smallTextStyle, color = blueDark),
            isStrikethrough = item.isEventNotActive()
        )
    }
}

@Composable
private fun EventDetails(
    viewModel: DataSummaryScreenViewModel,
    item: SubjectLivelihoodEventSummaryUiModel,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
    ) {
        item.transactionAmount?.let {
            Row(modifier = Modifier.weight(1f)) {
                TextWithPaddingEnd(
                    text = viewModel.stringResource(
                        resId = R.string.amount
                    ),
                    style = getTextColor(smallTextStyle, color = eventTextColor)
                )
                StrikethroughText(
                    text = getAmountForEvent(item),
                    textStyle = getAmountColorForEvent(item),
                    isStrikethrough = item.isEventNotActive()
                )
            }
        }

        item.assetCount?.let {
            Row(modifier = Modifier.weight(1f)) {
                TextWithPaddingEnd(
                    text = viewModel.stringResource(resId = R.string.asset),
                    style = getTextColor(smallTextStyle, color = eventTextColor)
                )
                StrikethroughText(
                    text = getAssetCountForEvent(item),
                    textStyle = getTextColor(newMediumTextStyle),
                    isStrikethrough = item.isEventNotActive()
                )
            }
        }
        if (item.status != 2) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "ArrowForward Icon",
                modifier = Modifier
                    .size(dimen_24_dp)
                    .clickable { onClick() },
                tint = blueDark
            )
        } else {
            Spacer(modifier = Modifier.size(dimen_24_dp))
        }

    }
}

fun getAssetCountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.assetJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OUTFLOW.name.toLowerCase()) == true
    ) {
        "- ${item.assetCount}"
    } else {
        "+ ${item.assetCount}"
    }
}

fun getAmountColorForEvent(item: SubjectLivelihoodEventSummaryUiModel): TextStyle {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OUTFLOW.name.toLowerCase()) == true
    ) {
        newMediumTextStyle.copy(color = redOffline)
    } else
        newMediumTextStyle.copy(color = greenOnline)

}

fun getAmountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OUTFLOW.name.toLowerCase()) == true
    ) {
        "- ₹ ${item.transactionAmount}"
    } else {
        "+ ₹ ${item.transactionAmount}"
    }
}

@Composable
private fun TextWithPaddingEnd(text: String, style: TextStyle) {
    StrikethroughText(
        modifier = Modifier.padding(end = dimen_5_dp),
        text = text,
        textStyle = style
    )
}

@Composable
private fun AddEventButton(
    viewModel: DataSummaryScreenViewModel,
    onAddEventButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    ButtonPositive(
        buttonTitle = viewModel.stringResource(R.string.add_event),
        isActive = true,
        isArrowRequired = true
    ) {
        onAddEventButtonClicked()
    }
}

fun SubjectLivelihoodEventSummaryUiModel.isEventNotActive(): Boolean {
    return this.status == 2
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()
    countMap.put(SubTabs.All, 1)
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
    }
}

@Composable
fun TaskCompletionMessageDemo(message: String) {
    var showMessage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter,

        ) {
        // Trigger to simulate a completed task
        TaskCompletionTrigger { showMessage = true }

        if (showMessage) {
            TaskCompletionMessage(
                message = message,
                onDismiss = { showMessage = false }
            )
        }
    }
}

@Composable
fun TaskCompletionTrigger(onComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        // Simulate a delay for task completion
        delay(10)
        onComplete()
    }
}

@Composable
fun TaskCompletionMessage(
    message: String,
    onDismiss: () -> Unit,
    durationMillis: Int = 4000
) {
    var startAnimation by remember { mutableStateOf(false) }
    val animationOffset = animateIntOffsetAsState(
        targetValue = if (startAnimation) IntOffset(0, 0) else IntOffset(0, -500),
        animationSpec = tween(durationMillis / 2)
    )

    LaunchedEffect(Unit) {
        launch {
            startAnimation = true
            delay(durationMillis.toLong()) // Display the message for a specific duration
            startAnimation = false
            delay(500) // Allow time for the animation to finish
            onDismiss() // Notify that the message should be dismissed
        }
    }
    AnimatedVisibility(
        visible = startAnimation,
        enter = fadeIn(tween(durationMillis / 2)),
        exit = fadeOut(tween(durationMillis / 2))
    ) {

        Box(
            modifier = Modifier
                .padding(horizontal = dimen_10_dp)
                .fillMaxWidth()
                .offset { animationOffset.value }
                .background(
                    color = taskCompletionBannerBgColor,
                    shape = RoundedCornerShape(dimen_5_dp)
                )
                .padding(all = dimen_15_dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    tint = Color.White,
                    contentDescription = "Completed tick",
                    modifier = Modifier.padding(horizontal = dimen_10_dp)
                )
                Text(
                    text = message,
                    modifier = Modifier
                        .wrapContentSize(),
                    style = h6Bold
                )
            }
        }
    }
}



