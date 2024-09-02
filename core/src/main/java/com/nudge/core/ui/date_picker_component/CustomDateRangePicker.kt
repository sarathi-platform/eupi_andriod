package com.nudge.core.ui.date_picker_component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
fun CustomDateRangePicker(
    state: CustomDateRangePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: CustomDatePickerFormatter = remember { CustomDatePickerFormatter() },
    dateValidator: (Long) -> Boolean = { true },
    title: (@Composable () -> Unit)? = {
        /*CustomDateRangePickerDefaults.DateRangePickerTitle(
            state = state,
            modifier = Modifier.padding(DateRangePickerTitlePadding)
        )*/
    },
    headline: (@Composable () -> Unit)? = {
        /*CustomDateRangePickerDefaults.DateRangePickerHeadline(
            state,
            dateFormatter,
            modifier = Modifier.padding(DateRangePickerHeadlinePadding)
        )*/
    },
    showModeToggle: Boolean = false,
    colors: CustomDatePickerColors = CustomDatePickerDefaults.colors()
) {
    DateEntryContainer(
        modifier = modifier,
        title = title,
        headline = headline,
        modeToggleButton = if (showModeToggle) {
            {
                DisplayModeToggleButton(
                    modifier = Modifier.padding(DatePickerModeTogglePadding),
                    displayMode = state.displayMode,
                    onDisplayModeChange = { displayMode ->
                        state.stateData.switchDisplayMode(
                            displayMode
                        )
                    }
                )
            }
        } else {
            null
        },
        headlineTextStyle = MaterialTheme.typography.titleLarge,
        headerMinHeight = RangeSelectionHeaderContainerHeight -
                HeaderHeightOffset,
        colors = colors
    ) {
        SwitchableDateEntryContent(
            state = state,
            dateFormatter = dateFormatter,
            dateValidator = dateValidator,
            colors = colors
        )
    }
}

@Composable
@ExperimentalMaterial3Api
fun rememberDateRangePickerState(
    @Suppress("AutoBoxing") initialSelectedStartDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialSelectedEndDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? =
        initialSelectedStartDateMillis,
    yearRange: IntRange = CustomDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): CustomDateRangePickerState = rememberSaveable(
    saver = CustomDateRangePickerState.Saver()
) {
    CustomDateRangePickerState(
        initialSelectedStartDateMillis = initialSelectedStartDateMillis,
        initialSelectedEndDateMillis = initialSelectedEndDateMillis,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        yearRange = yearRange,
        initialDisplayMode = initialDisplayMode
    )
}

@ExperimentalMaterial3Api
@Stable
class CustomDateRangePickerState private constructor(val stateData: CustomStateData) {

    constructor(
        @Suppress("AutoBoxing") initialSelectedStartDateMillis: Long?,
        @Suppress("AutoBoxing") initialSelectedEndDateMillis: Long?,
        @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long?,
        yearRange: IntRange,
        initialDisplayMode: DisplayMode
    ) : this(
        CustomStateData(
            initialSelectedStartDateMillis = initialSelectedStartDateMillis,
            initialSelectedEndDateMillis = initialSelectedEndDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode,
        )
    )

    val selectedStartDateMillis: Long?
        @Suppress("AutoBoxing") get() = stateData.selectedStartDate.value?.utcTimeMillis

    val selectedEndDateMillis: Long?
        @Suppress("AutoBoxing") get() = stateData.selectedEndDate.value?.utcTimeMillis

    fun setSelection(
        @Suppress("AutoBoxing") startDateMillis: Long?,
        @Suppress("AutoBoxing") endDateMillis: Long?
    ) {
        stateData.setSelection(startDateMillis = startDateMillis, endDateMillis = endDateMillis)
    }

    var displayMode by stateData.displayMode

    companion object {

        fun Saver(): Saver<CustomDateRangePickerState, *> = Saver(
            save = { with(CustomStateData.Saver()) { save(it.stateData) } },
            restore = { value ->
                CustomDateRangePickerState(with(CustomStateData.Saver()) { restore(value)!! })
            }
        )
    }
}

@ExperimentalMaterial3Api
@Stable
object CustomDateRangePickerDefaults {

    @Composable
    fun DateRangePickerTitle(
        state: CustomDateRangePickerState,
        modifier: Modifier = Modifier
    ) {
        when (state.displayMode) {
            DisplayMode.Picker -> Text(
                "Select Date Range",
                modifier = modifier
            )

            DisplayMode.Input -> Text(
                "DateRangeInputTitle",
                modifier = modifier
            )
        }
    }

    @Composable
    fun DateRangePickerHeadline(
        state: CustomDateRangePickerState,
        dateFormatter: CustomDatePickerFormatter,
        modifier: Modifier = Modifier
    ) {
        val startDateText = "Start date"
        val endDateText = "End date"
        DateRangePickerHeadline(
            state = state,
            dateFormatter = dateFormatter,
            modifier = modifier,
            startDateText = startDateText,
            endDateText = endDateText,
            startDatePlaceholder = { Text(text = startDateText) },
            endDatePlaceholder = { Text(text = endDateText) },
            datesDelimiter = { Text(text = "-") },
        )
    }

    @Composable
    private fun DateRangePickerHeadline(
        state: CustomDateRangePickerState,
        dateFormatter: CustomDatePickerFormatter,
        modifier: Modifier,
        startDateText: String,
        endDateText: String,
        startDatePlaceholder: @Composable () -> Unit,
        endDatePlaceholder: @Composable () -> Unit,
        datesDelimiter: @Composable () -> Unit,
    ) {
        with(state.stateData) {
            val defaultLocale = Locale.ENGLISH
            val formatterStartDate = dateFormatter.formatDate(
                date = selectedStartDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale
            )

            val formatterEndDate = dateFormatter.formatDate(
                date = selectedEndDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale
            )

            val verboseStartDateDescription = dateFormatter.formatDate(
                date = selectedStartDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale,
                forContentDescription = true
            ) ?: when (displayMode.value) {
                DisplayMode.Picker -> "DatePickerNoSelectionDescription"
                DisplayMode.Input -> "DateInputNoInputDescription"
                else -> ""
            }

            val verboseEndDateDescription = dateFormatter.formatDate(
                date = selectedEndDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale,
                forContentDescription = true
            ) ?: when (displayMode.value) {
                DisplayMode.Picker -> "DatePickerNoSelectionDescription"
                DisplayMode.Input -> "DateInputNoInputDescription"
                else -> ""
            }

            val startHeadlineDescription = "$startDateText: $verboseStartDateDescription"
            val endHeadlineDescription = "$endDateText: $verboseEndDateDescription"

            Row(
                modifier = modifier.clearAndSetSemantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = "$startHeadlineDescription, $endHeadlineDescription"
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (formatterStartDate != null) {
                    Text(text = formatterStartDate)
                } else {
                    startDatePlaceholder()
                }
                datesDelimiter()
                if (formatterEndDate != null) {
                    Text(text = formatterEndDate)
                } else {
                    endDatePlaceholder()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableDateEntryContent(
    state: CustomDateRangePickerState,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors
) {

    Crossfade(
        targetState = state.displayMode,
        animationSpec = spring(),
        modifier = Modifier.semantics { isContainer = true },
        label = "SwitchableDateEntryContent"
    ) { mode ->
        when (mode) {
            DisplayMode.Picker -> CustomDateRangePickerContent(
                stateData = state.stateData,
                dateFormatter = dateFormatter,
                dateValidator = dateValidator,
                colors = colors
            )

            DisplayMode.Input -> {
                /*DateRangeInputContent(
                    stateData = state.stateData,
                    dateFormatter = dateFormatter,
                    dateValidator = dateValidator,
                )*/
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDateRangePickerContent(
    stateData: CustomStateData,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors
) {
    val monthsListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = stateData.displayedMonthIndex
        )

    val onDateSelected = { dateInMillis: Long ->
        updateDateSelection(stateData, dateInMillis)
    }
    Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
        WeekDays(colors, stateData.calendarModel)
        VerticalMonthsList(
            onDateSelected = onDateSelected,
            stateData = stateData,
            lazyListState = monthsListState,
            dateFormatter = dateFormatter,
            dateValidator = dateValidator,
            colors = colors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerticalMonthsList(
    onDateSelected: (dateInMillis: Long) -> Unit,
    stateData: CustomStateData,
    lazyListState: LazyListState,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors
) {
    val today = stateData.calendarModel.today
    val firstMonth = remember(stateData.yearRange) {
        stateData.calendarModel.getMonth(
            year = stateData.yearRange.first,
            month = 1 // January
        )
    }
    ProvideTextStyle(
        MaterialTheme.typography.titleSmall
    ) {
        val coroutineScope = rememberCoroutineScope()
        val scrollToPreviousMonthLabel = "DateRangePickerScrollToShowPreviousMonth"
        val scrollToNextMonthLabel = "DateRangePickerScrollToShowNextMonth"
        LazyColumn(
            // Apply this to have the screen reader traverse outside the visible list of months
            // and not scroll them by default.
            modifier = Modifier.semantics {
                verticalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
            },
            state = lazyListState
        ) {
            items(stateData.totalMonthsInRange) {
                val month =
                    stateData.calendarModel.plusMonths(
                        from = firstMonth,
                        addedMonthsCount = it
                    )
                Column(
                    modifier = Modifier.fillParentMaxWidth()
                ) {
                    Text(
                        text = dateFormatter.formatMonthYear(
                            month,
                            stateData.calendarModel,
                            Locale.ENGLISH
                        ) ?: "-",
                        modifier = Modifier
                            .padding(paddingValues = CalendarMonthSubheadPadding)
                            .clickable { /* no-op (needed for customActions to operate */ }
                            .semantics {
                                customActions = customScrollActions(
                                    state = lazyListState,
                                    coroutineScope = coroutineScope,
                                    scrollUpLabel = scrollToPreviousMonthLabel,
                                    scrollDownLabel = scrollToNextMonthLabel
                                )
                            },
                        color = colors.subheadContentColor
                    )
                    Month(
                        month = month,
                        onDateSelected = onDateSelected,
                        today = today,
                        stateData = stateData,
                        rangeSelectionEnabled = true,
                        dateValidator = dateValidator,
                        dateFormatter = dateFormatter,
                        colors = colors
                    )
                }
            }
        }
    }
    LaunchedEffect(lazyListState) {
        updateDisplayedMonth(lazyListState, stateData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun updateDateSelection(
    stateData: CustomStateData,
    dateInMillis: Long
) {
    with(stateData) {
        val date = calendarModel.getCanonicalDate(dateInMillis)
        val currentStart = selectedStartDate.value
        val currentEnd = selectedEndDate.value
        if ((currentStart == null && currentEnd == null) ||
            (currentStart != null && currentEnd != null) ||
            (currentStart != null && date < currentStart)
        ) {
            // Reset the selection to "start" only.
            selectedStartDate.value = date
            selectedEndDate.value = null
        } else if (currentStart != null && date >= currentStart) {
            selectedEndDate.value = date
        }
    }
}

val CalendarMonthSubheadPadding = PaddingValues(
    start = 24.dp,
    top = 20.dp,
    bottom = 8.dp
)

class CustomSelectedRangeInfo(
    val gridCoordinates: Pair<IntOffset, IntOffset>,
    val firstIsSelectionStart: Boolean,
    val lastIsSelectionEnd: Boolean
) {
    companion object {

        @OptIn(ExperimentalMaterial3Api::class)
        fun calculateRangeInfo(
            month: CustomCalendarMonth,
            startDate: CustomCalendarDate?,
            endDate: CustomCalendarDate?
        ): CustomSelectedRangeInfo? {
            if (startDate != null && endDate != null) {
                if (startDate.utcTimeMillis > month.endUtcTimeMillis ||
                    endDate.utcTimeMillis < month.startUtcTimeMillis
                ) {
                    return null
                }
                val firstIsSelectionStart = startDate.utcTimeMillis >= month.startUtcTimeMillis
                val lastIsSelectionEnd = endDate.utcTimeMillis <= month.endUtcTimeMillis
                val startGridItemOffset = if (firstIsSelectionStart) {
                    month.daysFromStartOfWeekToFirstOfMonth + startDate.dayOfMonth - 1
                } else {
                    month.daysFromStartOfWeekToFirstOfMonth
                }
                val endGridItemOffset = if (lastIsSelectionEnd) {
                    month.daysFromStartOfWeekToFirstOfMonth + endDate.dayOfMonth - 1
                } else {
                    month.daysFromStartOfWeekToFirstOfMonth + month.numberOfDays - 1
                }

                // Calculate the selected coordinates within the cells grid.
                val startCoordinates = IntOffset(
                    x = startGridItemOffset % DaysInWeek,
                    y = startGridItemOffset / DaysInWeek
                )
                val endCoordinates = IntOffset(
                    x = endGridItemOffset % DaysInWeek,
                    y = endGridItemOffset / DaysInWeek
                )
                return CustomSelectedRangeInfo(
                    Pair(startCoordinates, endCoordinates),
                    firstIsSelectionStart,
                    lastIsSelectionEnd
                )
            }
            return null
        }
    }
}

fun ContentDrawScope.customDrawRangeBackground(
    selectedRangeInfo: CustomSelectedRangeInfo,
    color: Color
) {
    // The LazyVerticalGrid is defined to space the items horizontally by
    // DaysHorizontalPadding (e.g. 4.dp). However, as the grid is not limited in
    // width, the spacing can go beyond that value, so this drawing takes this into
    // account.
    // TODO: Use the date's container width and height from the tokens once b/247694457 is resolved.
    val itemContainerWidth = RecommendedSizeForAccessibility.toPx()
    val itemContainerHeight = RecommendedSizeForAccessibility.toPx()
    val itemStateLayerHeight = DateStateLayerHeight.toPx()
    val stateLayerVerticalPadding = (itemContainerHeight - itemStateLayerHeight) / 2
    val horizontalSpaceBetweenItems =
        (this.size.width - DaysInWeek * itemContainerWidth) / DaysInWeek

    val (x1, y1) = selectedRangeInfo.gridCoordinates.first
    val (x2, y2) = selectedRangeInfo.gridCoordinates.second
    // The endX and startX are offset to include only half the item's width when dealing with first
    // and last items in the selection in order to keep the selection edges rounded.
    var startX = x1 * (itemContainerWidth + horizontalSpaceBetweenItems) +
            (if (selectedRangeInfo.firstIsSelectionStart) itemContainerWidth / 2 else 0f) +
            horizontalSpaceBetweenItems / 2
    val startY = y1 * itemContainerHeight + stateLayerVerticalPadding
    var endX = x2 * (itemContainerWidth + horizontalSpaceBetweenItems) +
            (if (selectedRangeInfo.lastIsSelectionEnd) itemContainerWidth / 2 else itemContainerWidth) +
            horizontalSpaceBetweenItems / 2
    val endY = y2 * itemContainerHeight + stateLayerVerticalPadding

    val isRtl = layoutDirection == LayoutDirection.Rtl
    // Adjust the start and end in case the layout is RTL.
    if (isRtl) {
        startX = this.size.width - startX
        endX = this.size.width - endX
    }

    // Draw the first row background
    drawRect(
        color = color,
        topLeft = Offset(startX, startY),
        size = Size(
            width = when {
                y1 == y2 -> endX - startX
                isRtl -> -startX
                else -> this.size.width - startX
            },
            height = itemStateLayerHeight
        )
    )

    if (y1 != y2) {
        for (y in y2 - y1 - 1 downTo 1) {
            // Draw background behind the rows in between.
            drawRect(
                color = color,
                topLeft = Offset(0f, startY + (y * itemContainerHeight)),
                size = Size(
                    width = this.size.width,
                    height = itemStateLayerHeight
                )
            )
        }
        // Draw the last row selection background
        val topLeftX = if (layoutDirection == LayoutDirection.Ltr) 0f else this.size.width
        drawRect(
            color = color,
            topLeft = Offset(topLeftX, endY),
            size = Size(
                width = if (isRtl) endX - this.size.width else endX,
                height = itemStateLayerHeight
            )
        )
    }
}

private fun customScrollActions(
    state: LazyListState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) {
            false
        } else {
            coroutineScope.launch {
                state.scrollToItem(state.firstVisibleItemIndex - 1)
            }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) {
            false
        } else {
            coroutineScope.launch {
                state.scrollToItem(state.firstVisibleItemIndex + 1)
            }
            true
        }
    }
    return listOf(
        CustomAccessibilityAction(
            label = scrollUpLabel,
            action = scrollUpAction
        ),
        CustomAccessibilityAction(
            label = scrollDownLabel,
            action = scrollDownAction
        )
    )
}

val DateRangePickerTitlePadding = PaddingValues(start = 64.dp, end = 12.dp)
val DateRangePickerHeadlinePadding =
    PaddingValues(start = 64.dp, end = 12.dp, bottom = 12.dp)

// An offset that is applied to the token value for the RangeSelectionHeaderContainerHeight. The
// implementation does not render a "Save" and "X" buttons by default, so we don't take those into
// account when setting the header's max height.
val HeaderHeightOffset = 60.dp

val RangeSelectionHeaderContainerHeight = 128.0.dp