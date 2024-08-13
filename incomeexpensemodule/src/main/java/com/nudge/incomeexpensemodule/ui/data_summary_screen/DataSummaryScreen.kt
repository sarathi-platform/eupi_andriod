package com.nudge.incomeexpensemodule.ui.data_summary_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.incomeexpensemodule.R
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.ui.commonUi.CustomDateRangePickerBottomSheetComponent
import com.nudge.core.ui.commonUi.CustomDateRangePickerDisplay
import com.nudge.core.ui.commonUi.CustomSubTabLayoutWithCallBack
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.SheetHeight
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.rememberCustomDateRangePickerSheetState
import com.nudge.core.ui.commonUi.rememberDateRangePickerBottomSheetProperties
import com.nudge.core.ui.commonUi.rememberDateRangePickerProperties
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.incomeCardBorderColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.searchFieldBg
import com.nudge.core.ui.theme.yellowBg
import com.nudge.core.value
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
import com.nudge.incomeexpensemodule.navigation.navigateToAddEventScreen
import com.nudge.incomeexpensemodule.ui.AssetsDialog
import com.nudge.incomeexpensemodule.ui.component.SingleSelectDropDown
import com.nudge.incomeexpensemodule.ui.component.TotalIncomeExpenseAssetSummaryView
import com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel.DataSummaryScreenViewModel
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
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.setPreviousScreenData(subjectId)
        viewModel.onEvent(InitDataEvent.InitDataSummaryScreenState(subjectId = subjectId))
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

    val dateRangePickerProperties = rememberDateRangePickerProperties()

    val scope = rememberCoroutineScope()

    if (viewModel.showAssetDialog.value) {
        AssetsDialog(
            viewModel.incomeExpenseSummaryUiModel[viewModel.selectedLivelihood.value],
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
                BottomAppBar(
                    backgroundColor = Color.White,
                    elevation = 10.dp
                ) {
                    AddEventButton() {
                        navigateToAddEventScreen(
                            navController = navController,
                            subjectName = subjectName,
                            subjectId = subjectId,
                            transactionID = UUID.randomUUID().toString(),
                            showDeleteButton = false
                        )
                    }

                }
            },
            onContentUI = { a, b, c ->

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    if (viewModel.areEventsNotAvailableForSubject.value) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AddEventButton() {
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
                                }
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
    onShowModeClicked: () -> Unit
) {
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
            label = stringResource(R.string.date_range_picker_label_text)
        ) {
            dateRangePickerClicked()
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    DropDownContainer(viewModel.livelihoodDropdownList.toList()) {
        viewModel.onEvent(DataSummaryScreenEvents.FilterDataForLivelihood(it))
    }
    Spacer(modifier = Modifier.height(16.dp))
    HeaderSection(viewModel.incomeExpenseSummaryUiModel[viewModel.selectedLivelihood.value]!!) {
        viewModel.onEvent(DialogEvents.ShowDialogEvent(true))
    }
    Spacer(modifier = Modifier.height(16.dp))
    EventsListHeaderWithDropDownFilter(
        viewModel.eventsSubFilterList,
        selectedValue = viewModel.selectedEventsSubFilter.value,
        showMoreItems
    ) { selectedFilterId ->
        viewModel.onEvent(DataSummaryScreenEvents.EventsSubFilterSelected(selectedFilterId))
    }

    Spacer(modifier = Modifier.height(16.dp))
    EventView(
        viewModel.filteredSubjectLivelihoodEventSummaryUiModelList.toList()
            .sortedByDescending { it.date },
        viewModel.livelihoodEventMap,
        viewModel.selectedLivelihood.value,
        showMoreItems = showMoreItems,
        onEventItemClicked = onEventItemClicked,
        onShowModeClicked = {
            onShowModeClicked()
        }
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
    SingleSelectDropDown(sources = livelihoodList, selectOptionText = livelihoodList.first().id) {
        onValueSelected(it)
    }

}

@Composable
fun HeaderSection(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel,
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
    eventSubFilterSelected: List<ValuesDto>,
    selectedValue: Int,
    showMoreItems: Boolean,
    onEventSubFilterSelected: (selectedFilterId: Int) -> Unit
) {
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
            if (showMoreItems) "All Events:" else "Last $DEFAULT_EVENT_LIST_VIEW_SIZE events:",
            style = getTextColor(defaultTextStyle)
        )
        MeasureUnconstrainedViewWidth(viewToMeasure = { Text(text = selectedOptionValue.value) }) {
            SingleSelectDropDown(
                sources = sources,
                selectOptionText = selectedOptionValue.id,
                modifier = Modifier.width(it + dimen_60_dp + dimen_16_dp)
            ) { selectValue ->

                selectedOptionValue = sources.findById(selectValue) ?: sources[0]
                onEventSubFilterSelected(selectValue)

            }
        }

    }
}


@Composable
fun ShowMoreButton(showMoreItems: Boolean, onShowModeClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }, horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                onShowModeClicked()
            },
            modifier = Modifier
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = borderGreyLight,
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (showMoreItems) "Show Less" else "Show more",
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
    filteredSubjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventSummaryUiModel>,
    eventsList: Map<Int, List<LivelihoodEventUiModel>>,
    selectedLivelihoodId: Int,
    showMoreItems: Boolean,
    onEventItemClicked: (transactionId: String) -> Unit,
    onShowModeClicked: () -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimen_10_dp), modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_10_dp)
    ) {

        itemsIndexed(
            filteredSubjectLivelihoodEventSummaryUiModelList.toList()
                .take(DEFAULT_EVENT_LIST_VIEW_SIZE)
        ) { index, subjectLivelihoodEventSummaryUiModel ->
            Column(modifier = Modifier
                .clickable {
                    onEventItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                }
            ) {
                EventHeader(subjectLivelihoodEventSummaryUiModel, eventsList[selectedLivelihoodId])
                EventDetails(subjectLivelihoodEventSummaryUiModel)
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
                        Column(modifier = Modifier
                            .clickable {
                                onEventItemClicked(subjectLivelihoodEventSummaryUiModel.transactionId.value())
                            }
                        ) {
                            EventHeader(
                                subjectLivelihoodEventSummaryUiModel,
                                eventsList[selectedLivelihoodId]
                            )
                            EventDetails(
                                subjectLivelihoodEventSummaryUiModel,
                            )
                        }
                    }
                }
            }
        }

        if (filteredSubjectLivelihoodEventSummaryUiModelList.size > DEFAULT_EVENT_LIST_VIEW_SIZE) {
            item {
                ShowMoreButton(showMoreItems) {

                    onShowModeClicked()
                }
            }
        }
        item {
            CustomVerticalSpacer(size = dimen_60_dp)
        }

    }
}

const val DEFAULT_EVENT_LIST_VIEW_SIZE = 3

@Composable
private fun EventHeader(
    item: SubjectLivelihoodEventSummaryUiModel,
    livelihoodEventUiModels: List<LivelihoodEventUiModel>?
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            TextWithPaddingEnd(
                text = "Event:",
                style = getTextColor(quesOptionTextStyle)
            )
            Text(
                text = livelihoodEventUiModels.find(item.livelihoodEventId.value())?.name.value(),
                style = getTextColor(newMediumTextStyle)
            )
        }
        Text(
            text = item.date.getDate(pattern = DD_MMM_YYYY_FORMAT),
            style = getTextColor(quesOptionTextStyle)
        )

    }
}

@Composable
private fun EventDetails(
    item: SubjectLivelihoodEventSummaryUiModel,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        item.transactionAmount?.let {
            Row {
                TextWithPaddingEnd(
                    text = "Amount: ",
                    style = getTextColor(quesOptionTextStyle)
                )
                Text(
                    text = getAmountForEvent(item),
                    style = getAmountColorForEvent(item)
                )
            }
        }

        item.assetCount?.let {
            Row {
                TextWithPaddingEnd(
                    text = "Assets: ",
                    style = getTextColor(quesOptionTextStyle)
                )
                Text(
                    text = getAssetCountForEvent(item),
                    style = getTextColor(newMediumTextStyle)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "ArrowForward Icon",
            modifier = Modifier.size(dimen_24_dp),
            tint = blueDark
        )

    }
}

fun getAssetCountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.assetJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        "- ${item.assetCount}"
    } else {
        "+ ${item.assetCount}"
    }
}

fun getAmountColorForEvent(item: SubjectLivelihoodEventSummaryUiModel): TextStyle {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        newMediumTextStyle.copy(color = redOffline)
    } else
        newMediumTextStyle.copy(color = greenOnline)

}

fun getAmountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        "- ₹ ${item.transactionAmount}"
    } else {
        "+ ₹ ${item.transactionAmount}"
    }
}

@Composable
private fun TextWithPaddingEnd(text: String, style: TextStyle) {
    Text(
        modifier = Modifier.padding(end = dimen_5_dp),
        text = text,
        style = style
    )
}

@Composable
private fun AddEventButton(onAddEventButtonClicked: () -> Unit) {
    ButtonPositive(buttonTitle = "Add Event", isActive = true, isArrowRequired = true) {
        onAddEventButtonClicked()
    }
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
//        DataSummaryView(navController = rememberNavController(), 0, "", countMap = countMap)
    }
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Dp) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose("viewToMeasure", viewToMeasure)[0]
            .measure(Constraints()).width.toDp()

        val contentPlaceable = subcompose("content") {
            content(measuredWidth)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}