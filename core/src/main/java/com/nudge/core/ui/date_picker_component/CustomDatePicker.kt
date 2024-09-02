package com.nudge.core.ui.date_picker_component

import android.os.Build
import android.text.format.DateFormat
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
fun CustomDatePicker(
    state: CustomDatePickerState,
    modifier: Modifier = Modifier,
    dateFormatter: CustomDatePickerFormatter = remember { CustomDatePickerFormatter() },
    dateValidator: (Long) -> Boolean = { true },
    title: (@Composable () -> Unit)? = {
        CustomDatePickerDefaults.DatePickerTitle(
            state,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        CustomDatePickerDefaults.DatePickerHeadline(
            state,
            dateFormatter,
            modifier = Modifier.padding(DatePickerHeadlinePadding)
        )
    },
    showModeToggle: Boolean = true,
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
                    },
                )
            }
        } else {
            null
        },
        headlineTextStyle = MaterialTheme.typography.headlineLarge,
        headerMinHeight = HeaderContainerHeight,
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
fun rememberDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = CustomDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): CustomDatePickerState = rememberSaveable(
    saver = CustomDatePickerState.Saver()
) {
    CustomDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        yearRange = yearRange,
        initialDisplayMode = initialDisplayMode
    )
}

@ExperimentalMaterial3Api
@Stable
class CustomDatePickerState private constructor(val stateData: CustomStateData) {

    constructor(
        @Suppress("AutoBoxing") initialSelectedDateMillis: Long?,
        @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long?,
        yearRange: IntRange,
        initialDisplayMode: DisplayMode
    ) : this(
        CustomStateData(
            initialSelectedStartDateMillis = initialSelectedDateMillis,
            initialSelectedEndDateMillis = null,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode,
        )
    )

    val selectedDateMillis: Long?
        @Suppress("AutoBoxing") get() = stateData.selectedStartDate.value?.utcTimeMillis

    fun setSelection(@Suppress("AutoBoxing") dateMillis: Long?) {
        stateData.setSelection(startDateMillis = dateMillis, endDateMillis = null)
    }

    var displayMode by stateData.displayMode

    companion object {
        fun Saver(): Saver<CustomDatePickerState, *> = Saver(
            save = { with(CustomStateData.Saver()) { save(it.stateData) } },
            restore = { value -> CustomDatePickerState(with(CustomStateData.Saver()) { restore(value)!! }) }
        )
    }
}

@ExperimentalMaterial3Api
@Stable
object CustomDatePickerDefaults {

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        headlineContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        weekdayContentColor: Color = MaterialTheme.colorScheme.onSurface,
        subheadContentColor: Color =
            MaterialTheme.colorScheme.onSurfaceVariant,
        yearContentColor: Color =
            MaterialTheme.colorScheme.onSurfaceVariant,
        currentYearContentColor: Color = MaterialTheme.colorScheme.primary,
        selectedYearContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        selectedYearContainerColor: Color = MaterialTheme.colorScheme.primary,
        dayContentColor: Color = MaterialTheme.colorScheme.onSurface,
        // TODO: Missing token values for the disabled colors.
        disabledDayContentColor: Color = dayContentColor.copy(alpha = 0.38f),
        selectedDayContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        // TODO: Missing token values for the disabled colors.
        disabledSelectedDayContentColor: Color = selectedDayContentColor.copy(alpha = 0.38f),
        selectedDayContainerColor: Color = MaterialTheme.colorScheme.primary,
        // TODO: Missing token values for the disabled colors.
        disabledSelectedDayContainerColor: Color = selectedDayContainerColor.copy(alpha = 0.38f),
        todayContentColor: Color = MaterialTheme.colorScheme.primary,
        todayDateBorderColor: Color = MaterialTheme.colorScheme.primary,
        dayInSelectionRangeContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        dayInSelectionRangeContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer
    ): CustomDatePickerColors =
        CustomDatePickerColors(
            containerColor = containerColor,
            titleContentColor = titleContentColor,
            headlineContentColor = headlineContentColor,
            weekdayContentColor = weekdayContentColor,
            subheadContentColor = subheadContentColor,
            yearContentColor = yearContentColor,
            currentYearContentColor = currentYearContentColor,
            selectedYearContentColor = selectedYearContentColor,
            selectedYearContainerColor = selectedYearContainerColor,
            dayContentColor = dayContentColor,
            disabledDayContentColor = disabledDayContentColor,
            selectedDayContentColor = selectedDayContentColor,
            disabledSelectedDayContentColor = disabledSelectedDayContentColor,
            selectedDayContainerColor = selectedDayContainerColor,
            disabledSelectedDayContainerColor = disabledSelectedDayContainerColor,
            todayContentColor = todayContentColor,
            todayDateBorderColor = todayDateBorderColor,
            dayInSelectionRangeContentColor = dayInSelectionRangeContentColor,
            dayInSelectionRangeContainerColor = dayInSelectionRangeContainerColor
        )

    @Composable
    fun DatePickerTitle(state: CustomDatePickerState, modifier: Modifier = Modifier) {
        when (state.displayMode) {
            DisplayMode.Picker -> Text(
                text = "Select Date",
                modifier = modifier
            )

            DisplayMode.Input -> Text(
                text = "DateInputTitle",
                modifier = modifier
            )
        }
    }

    @Composable
    fun DatePickerHeadline(
        state: CustomDatePickerState,
        dateFormatter: CustomDatePickerFormatter,
        modifier: Modifier = Modifier
    ) {
        with(state.stateData) {
            val defaultLocale = Locale.ENGLISH
            val formattedDate = dateFormatter.formatDate(
                date = selectedStartDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale
            )
            val verboseDateDescription = dateFormatter.formatDate(
                date = selectedStartDate.value,
                calendarModel = calendarModel,
                locale = defaultLocale,
                forContentDescription = true
            ) ?: when (displayMode.value) {
                DisplayMode.Picker -> "DatePickerNoSelectionDescription"
                DisplayMode.Input -> "DateInputNoInputDescription"
                else -> ""
            }

            val headlineText = formattedDate ?: when (displayMode.value) {
                DisplayMode.Picker -> "DatePickerHeadline"
                DisplayMode.Input -> "DateInputHeadline"
                else -> ""
            }

            val headlineDescription = when (displayMode.value) {
                DisplayMode.Picker -> "DatePickerHeadlineDescription"
                DisplayMode.Input -> "DateInputHeadlineDescription"
                else -> ""
            }.format(verboseDateDescription)

            Text(
                text = headlineText,
                modifier = modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = headlineDescription
                },
                maxLines = 1
            )
        }
    }

    @Composable
    fun rememberSnapFlingBehavior(
        lazyListState: LazyListState,
        decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay()
    ): FlingBehavior {
        val density = LocalDensity.current
        return remember(density) {
            CustomSnapFlingBehavior(
                lazyListState = lazyListState,
                decayAnimationSpec = decayAnimationSpec,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                density = density
            )
        }
    }

    val YearRange: IntRange = IntRange(1900, 2100)

    val TonalElevation: Dp = ContainerElevation

    val shape: Shape @Composable get() = RoundedCornerShape(8.dp)

    const val YearMonthSkeleton: String = "yMMMM"

    const val YearAbbrMonthDaySkeleton: String = "yMMMd"

    const val YearMonthWeekdayDaySkeleton: String = "yMMMMEEEEd"
}

@ExperimentalMaterial3Api
@Immutable
class CustomDatePickerColors constructor(
    val containerColor: Color,
    val titleContentColor: Color,
    val headlineContentColor: Color,
    val weekdayContentColor: Color,
    val subheadContentColor: Color,
    private val yearContentColor: Color,
    private val currentYearContentColor: Color,
    private val selectedYearContentColor: Color,
    private val selectedYearContainerColor: Color,
    private val dayContentColor: Color,
    private val disabledDayContentColor: Color,
    private val selectedDayContentColor: Color,
    private val disabledSelectedDayContentColor: Color,
    private val selectedDayContainerColor: Color,
    private val disabledSelectedDayContainerColor: Color,
    private val todayContentColor: Color,
    val todayDateBorderColor: Color,
    val dayInSelectionRangeContainerColor: Color,
    private val dayInSelectionRangeContentColor: Color,
) {

    @Composable
    fun dayContentColor(
        isToday: Boolean,
        selected: Boolean,
        inRange: Boolean,
        enabled: Boolean
    ): State<Color> {
        val target = when {
            selected && enabled -> selectedDayContentColor
            selected && !enabled -> disabledSelectedDayContentColor
            inRange && enabled -> dayInSelectionRangeContentColor
            inRange && !enabled -> disabledDayContentColor
            isToday -> todayContentColor
            enabled -> dayContentColor
            else -> disabledDayContentColor
        }

        return if (inRange) {
            rememberUpdatedState(target)
        } else {
            // Animate the content color only when the day is not in a range.
            animateColorAsState(
                target,
                tween(durationMillis = DurationShort2.toInt()),
                label = "dayContainerColor"
            )
        }
    }

    @Composable
    fun dayContainerColor(
        selected: Boolean,
        enabled: Boolean,
        animate: Boolean
    ): State<Color> {
        val target = if (selected) {
            if (enabled) selectedDayContainerColor else disabledSelectedDayContainerColor
        } else {
            Color.Transparent
        }
        return if (animate) {
            animateColorAsState(
                target,
                tween(durationMillis = DurationShort2.toInt()),
                label = "dayContainerColor"
            )
        } else {
            rememberUpdatedState(target)
        }
    }

    @Composable
    fun yearContentColor(currentYear: Boolean, selected: Boolean): State<Color> {
        val target = if (selected) {
            selectedYearContentColor
        } else if (currentYear) {
            currentYearContentColor
        } else {
            yearContentColor
        }

        return animateColorAsState(
            target,
            tween(durationMillis = DurationShort2.toInt()),
            label = "yearContentColor"
        )
    }

    @Composable
    fun yearContainerColor(selected: Boolean): State<Color> {
        val target = if (selected) selectedYearContainerColor else Color.Transparent
        return animateColorAsState(
            target,
            tween(durationMillis = DurationShort2.toInt()),
            label = "yearContainerColor"
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CustomDatePickerColors) return false
        if (containerColor != other.containerColor) return false
        if (titleContentColor != other.titleContentColor) return false
        if (headlineContentColor != other.headlineContentColor) return false
        if (weekdayContentColor != other.weekdayContentColor) return false
        if (subheadContentColor != other.subheadContentColor) return false
        if (yearContentColor != other.yearContentColor) return false
        if (currentYearContentColor != other.currentYearContentColor) return false
        if (selectedYearContentColor != other.selectedYearContentColor) return false
        if (selectedYearContainerColor != other.selectedYearContainerColor) return false
        if (dayContentColor != other.dayContentColor) return false
        if (disabledDayContentColor != other.disabledDayContentColor) return false
        if (selectedDayContentColor != other.selectedDayContentColor) return false
        if (disabledSelectedDayContentColor != other.disabledSelectedDayContentColor) return false
        if (selectedDayContainerColor != other.selectedDayContainerColor) return false
        if (disabledSelectedDayContainerColor != other.disabledSelectedDayContainerColor) {
            return false
        }
        if (todayContentColor != other.todayContentColor) return false
        if (todayDateBorderColor != other.todayDateBorderColor) return false
        if (dayInSelectionRangeContainerColor != other.dayInSelectionRangeContainerColor) {
            return false
        }
        if (dayInSelectionRangeContentColor != other.dayInSelectionRangeContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + titleContentColor.hashCode()
        result = 31 * result + headlineContentColor.hashCode()
        result = 31 * result + weekdayContentColor.hashCode()
        result = 31 * result + subheadContentColor.hashCode()
        result = 31 * result + yearContentColor.hashCode()
        result = 31 * result + currentYearContentColor.hashCode()
        result = 31 * result + selectedYearContentColor.hashCode()
        result = 31 * result + selectedYearContainerColor.hashCode()
        result = 31 * result + dayContentColor.hashCode()
        result = 31 * result + disabledDayContentColor.hashCode()
        result = 31 * result + selectedDayContentColor.hashCode()
        result = 31 * result + disabledSelectedDayContentColor.hashCode()
        result = 31 * result + selectedDayContainerColor.hashCode()
        result = 31 * result + disabledSelectedDayContainerColor.hashCode()
        result = 31 * result + todayContentColor.hashCode()
        result = 31 * result + todayDateBorderColor.hashCode()
        result = 31 * result + dayInSelectionRangeContainerColor.hashCode()
        result = 31 * result + dayInSelectionRangeContentColor.hashCode()
        return result
    }
}

@ExperimentalMaterial3Api
@Immutable
class CustomDatePickerFormatter constructor(
    val yearSelectionSkeleton: String = CustomDatePickerDefaults.YearMonthSkeleton,
    val selectedDateSkeleton: String = CustomDatePickerDefaults.YearAbbrMonthDaySkeleton,
    val selectedDateDescriptionSkeleton: String =
        CustomDatePickerDefaults.YearMonthWeekdayDaySkeleton
) {

    fun formatMonthYear(
        month: CustomCalendarMonth?,
        calendarModel: CustomCalendarModel,
        locale: Locale = Locale.ENGLISH
    ): String? {
        if (month == null) return null
        return calendarModel.formatWithSkeleton(month, yearSelectionSkeleton, locale)
    }

    fun formatDate(
        date: CustomCalendarDate?,
        calendarModel: CustomCalendarModel,
        locale: Locale = Locale.ENGLISH,
        forContentDescription: Boolean = false
    ): String? {
        if (date == null) return null
        return calendarModel.formatWithSkeleton(
            date, if (forContentDescription) {
                selectedDateDescriptionSkeleton
            } else {
                selectedDateSkeleton
            },
            locale
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CustomDatePickerFormatter) return false

        if (yearSelectionSkeleton != other.yearSelectionSkeleton) return false
        if (selectedDateSkeleton != other.selectedDateSkeleton) return false
        if (selectedDateDescriptionSkeleton != other.selectedDateDescriptionSkeleton) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearSelectionSkeleton.hashCode()
        result = 31 * result + selectedDateSkeleton.hashCode()
        result = 31 * result + selectedDateDescriptionSkeleton.hashCode()
        return result
    }
}

@Immutable
@JvmInline
@ExperimentalMaterial3Api
value class DisplayMode constructor(val value: Int) {

    companion object {
        val Picker = DisplayMode(0)

        val Input = DisplayMode(1)
    }

    override fun toString() = when (this) {
        Picker -> "Picker"
        Input -> "Input"
        else -> "Unknown"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class CustomStateData constructor(
    initialSelectedStartDateMillis: Long?,
    initialSelectedEndDateMillis: Long?,
    initialDisplayedMonthMillis: Long?,
    val yearRange: IntRange,
    initialDisplayMode: DisplayMode,
) {

    val calendarModel: CustomCalendarModel = GetCustomCalendarModel()

    var selectedStartDate = mutableStateOf<CustomCalendarDate?>(null)

    var selectedEndDate = mutableStateOf<CustomCalendarDate?>(null)

    init {
        setSelection(
            startDateMillis = initialSelectedStartDateMillis,
            endDateMillis = initialSelectedEndDateMillis
        )
    }

    var displayedMonth by mutableStateOf(
        if (initialDisplayedMonthMillis != null) {
            val month = calendarModel.getMonth(initialDisplayedMonthMillis)
            require(yearRange.contains(month.year)) {
                "The initial display month's year (${month.year}) is out of the years range of " +
                        "$yearRange."
            }
            month
        } else {
            currentMonth
        }
    )

    val currentMonth: CustomCalendarMonth
        get() = calendarModel.getMonth(calendarModel.today)

    var displayMode = mutableStateOf(initialDisplayMode)

    val displayedMonthIndex: Int
        get() = displayedMonth.indexIn(yearRange)

    val totalMonthsInRange: Int
        get() = (yearRange.last - yearRange.first + 1) * 12

    fun setSelection(startDateMillis: Long?, endDateMillis: Long?) {
        val startDate = if (startDateMillis != null) {
            calendarModel.getCanonicalDate(startDateMillis)
        } else {
            null
        }
        val endDate = if (endDateMillis != null) {
            calendarModel.getCanonicalDate(endDateMillis)
        } else {
            null
        }
        // Validate that both dates are within the valid years range.
        startDate?.let {
            require(yearRange.contains(it.year)) {
                "The provided start date year (${it.year}) is out of the years range of $yearRange."
            }
        }
        endDate?.let {
            require(yearRange.contains(it.year)) {
                "The provided end date year (${it.year}) is out of the years range of $yearRange."
            }
        }
        // Validate that an end date cannot be set without a start date.
        if (endDate != null) {
            requireNotNull(startDate) {
                "An end date was provided without a start date."
            }
            // Validate that the end date appears on or after the start date.
            require(startDate.utcTimeMillis <= endDate.utcTimeMillis) {
                "The provided end date appears before the start date."
            }
        }
        selectedStartDate.value = startDate
        selectedEndDate.value = endDate
    }

    fun switchDisplayMode(displayMode: DisplayMode) {
        // Update the displayed month, if needed, and change the mode to a  date-picker.
        selectedStartDate.value?.let {
            displayedMonth = calendarModel.getMonth(it)
        }
        // When toggling back from an input mode, it's possible that the user input an invalid
        // start date and a valid end date. If this is the case, and the start date is null, ensure
        // that the end date is also null.
        if (selectedStartDate.value == null && selectedEndDate.value != null) {
            selectedEndDate.value = null
        }
        this.displayMode.value = displayMode
    }

    companion object {

        fun Saver(): Saver<CustomStateData, Any> = listSaver(
            save = {
                listOf(
                    it.selectedStartDate.value?.utcTimeMillis,
                    it.selectedEndDate.value?.utcTimeMillis,
                    it.displayedMonth.startUtcTimeMillis,
                    it.yearRange.first,
                    it.yearRange.last,
                    it.displayMode.value.value
                )
            },
            restore = { value ->
                CustomStateData(
                    initialSelectedStartDateMillis = value[0] as Long?,
                    initialSelectedEndDateMillis = value[1] as Long?,
                    initialDisplayedMonthMillis = value[2] as Long?,
                    yearRange = IntRange(value[3] as Int, value[4] as Int),
                    initialDisplayMode = DisplayMode(value[5] as Int)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun GetCustomCalendarModel(): CustomCalendarModel {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CustomCalendarModelImpl()
    } else {
        CustomLegacyCalendarModelImpl()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEntryContainer(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    modeToggleButton: (@Composable () -> Unit)?,
    colors: CustomDatePickerColors,
    headlineTextStyle: TextStyle,
    headerMinHeight: Dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .sizeIn(minWidth = ContainerWidth)
            .semantics { isContainer = true }
    ) {
        DatePickerHeader(
            modifier = Modifier,
            title = title,
            titleContentColor = colors.titleContentColor,
            headlineContentColor = colors.headlineContentColor,
            minHeight = headerMinHeight
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val horizontalArrangement = when {
                    headline != null && modeToggleButton != null -> Arrangement.SpaceBetween
                    headline != null -> Arrangement.Start
                    else -> Arrangement.End
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (headline != null) {
                        ProvideTextStyle(value = headlineTextStyle) {
                            Box(modifier = Modifier.weight(1f)) {
                                headline()
                            }
                        }
                    }
                    modeToggleButton?.invoke()
                }
                // Display a divider only when there is a title, headline, or a mode toggle.
                if (title != null || headline != null || modeToggleButton != null) {
                    Divider()
                }
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeToggleButton(
    modifier: Modifier,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit
) {
    if (displayMode == DisplayMode.Picker) {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Input) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "DatePickerSwitchToInputMode"
            )
        }
    } else {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Picker) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "DatePickerSwitchToCalendarMode"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableDateEntryContent(
    state: CustomDatePickerState,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors
) {

    Crossfade(
        targetState = state.displayMode,
        animationSpec = spring(),
        modifier = Modifier.semantics { isContainer = true },
        label = ""
    ) { mode ->
        when (mode) {
            DisplayMode.Picker -> DatePickerContent(
                stateData = state.stateData,
                dateFormatter = dateFormatter,
                dateValidator = dateValidator,
                colors = colors
            )

            DisplayMode.Input -> {
                /*DateInputContent(
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
private fun DatePickerContent(
    stateData: CustomStateData,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors
) {
    val monthsListState =
        rememberLazyListState(initialFirstVisibleItemIndex = stateData.displayedMonthIndex)
    val coroutineScope = rememberCoroutineScope()

    val onDateSelected = { dateInMillis: Long ->
        stateData.selectedStartDate.value =
            stateData.calendarModel.getCanonicalDate(dateInMillis)
    }

    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }
    val defaultLocale = Locale.ENGLISH
    Column {
        MonthsNavigation(
            modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding),
            nextAvailable = monthsListState.canScrollForward,
            previousAvailable = monthsListState.canScrollBackward,
            yearPickerVisible = yearPickerVisible,
            yearPickerText = dateFormatter.formatMonthYear(
                month = stateData.displayedMonth,
                calendarModel = stateData.calendarModel,
                locale = defaultLocale
            ) ?: "-",
            onNextClicked = {
                coroutineScope.launch {
                    monthsListState.animateScrollToItem(
                        monthsListState.firstVisibleItemIndex + 1
                    )
                }
            },
            onPreviousClicked = {
                coroutineScope.launch {
                    monthsListState.animateScrollToItem(
                        monthsListState.firstVisibleItemIndex - 1
                    )
                }
            },
            onYearPickerButtonClicked = { yearPickerVisible = !yearPickerVisible }
        )

        Box {
            Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
                WeekDays(colors, stateData.calendarModel)
                HorizontalMonthsList(
                    onDateSelected = onDateSelected,
                    stateData = stateData,
                    lazyListState = monthsListState,
                    dateFormatter = dateFormatter,
                    dateValidator = dateValidator,
                    colors = colors
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = yearPickerVisible,
                modifier = Modifier.clipToBounds(),
                enter = expandVertically() + fadeIn(initialAlpha = 0.6f),
                exit = shrinkVertically() + fadeOut()
            ) {
                // Apply a paneTitle to make the screen reader focus on a relevant node after this
                // column is hidden and disposed.
                val yearsPaneTitle = "DatePickerYearPickerPaneTitle"
                Column(modifier = Modifier.semantics { paneTitle = yearsPaneTitle }) {
                    YearPicker(
                        // Keep the height the same as the monthly calendar + weekdays height, and
                        // take into account the thickness of the divider that will be composed
                        // below it.
                        modifier = Modifier
                            .requiredHeight(
                                RecommendedSizeForAccessibility * (MaxCalendarRows + 1) -
                                        DividerDefaults.Thickness
                            )
                            .padding(horizontal = DatePickerHorizontalPadding),
                        onYearSelected = { year ->
                            // Switch back to the monthly calendar and scroll to the selected year.
                            yearPickerVisible = !yearPickerVisible
                            coroutineScope.launch {
                                // Scroll to the selected year (maintaining the month of year).
                                // A LaunchEffect at the MonthsList will take care of rest and will
                                // update the state's displayedMonth to the month we scrolled to.
                                with(stateData) {
                                    monthsListState.scrollToItem(
                                        (year - yearRange.first) * 12 + displayedMonth.month - 1
                                    )
                                }
                            }
                        },
                        colors = colors,
                        stateData = stateData
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun DatePickerHeader(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    titleContentColor: Color,
    headlineContentColor: Color,
    minHeight: Dp,
    content: @Composable () -> Unit
) {
    // Apply a defaultMinSize only when the title is not null.
    val heightModifier =
        if (title != null) {
            Modifier.defaultMinSize(minHeight = minHeight)
        } else {
            Modifier
        }
    Column(
        modifier
            .fillMaxWidth()
            .then(heightModifier),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (title != null) {
            CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                val textStyle =
                    MaterialTheme.typography.labelLarge
                ProvideTextStyle(textStyle) {
                    Box(contentAlignment = Alignment.BottomStart) {
                        title()
                    }
                }
            }
        }
        CompositionLocalProvider(
            LocalContentColor provides headlineContentColor, content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorizontalMonthsList(
    onDateSelected: (dateInMillis: Long) -> Unit,
    stateData: CustomStateData,
    lazyListState: LazyListState,
    dateFormatter: CustomDatePickerFormatter,
    dateValidator: (Long) -> Boolean,
    colors: CustomDatePickerColors,
) {
    val today = stateData.calendarModel.today
    val firstMonth = remember(stateData.yearRange) {
        stateData.calendarModel.getMonth(
            year = stateData.yearRange.first,
            month = 1 // January
        )
    }
    LazyRow(
        // Apply this to prevent the screen reader from scrolling to the next or previous month, and
        // instead, traverse outside the Month composable when swiping from a focused first or last
        // day of the month.
        modifier = Modifier.semantics {
            horizontalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
        },
        state = lazyListState,
        flingBehavior = CustomDatePickerDefaults.rememberSnapFlingBehavior(lazyListState)
    ) {
        items(stateData.totalMonthsInRange) {
            val month =
                stateData.calendarModel.plusMonths(
                    from = firstMonth,
                    addedMonthsCount = it
                )
            Box(
                modifier = Modifier.fillParentMaxWidth()
            ) {
                Month(
                    month = month,
                    onDateSelected = onDateSelected,
                    today = today,
                    stateData = stateData,
                    rangeSelectionEnabled = false,
                    dateValidator = dateValidator,
                    dateFormatter = dateFormatter,
                    colors = colors
                )
            }
        }
    }

    LaunchedEffect(lazyListState) {
        updateDisplayedMonth(lazyListState, stateData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun updateDisplayedMonth(
    lazyListState: LazyListState,
    stateData: CustomStateData
) {
    snapshotFlow { lazyListState.firstVisibleItemIndex }.collect {
        val yearOffset = lazyListState.firstVisibleItemIndex / 12
        val month = lazyListState.firstVisibleItemIndex % 12 + 1
        with(stateData) {
            if (displayedMonth.month != month ||
                displayedMonth.year != yearRange.first + yearOffset
            ) {
                displayedMonth = calendarModel.getMonth(
                    year = yearRange.first + yearOffset,
                    month = month
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDays(colors: CustomDatePickerColors, calendarModel: CustomCalendarModel) {
    val firstDayOfWeek = calendarModel.firstDayOfWeek
    val weekdays = calendarModel.weekdayNames
    val dayNames = arrayListOf<Pair<String, String>>()
    // Start with firstDayOfWeek - 1 as the days are 1-based.
    for (i in firstDayOfWeek - 1 until weekdays.size) {
        dayNames.add(weekdays[i])
    }
    for (i in 0 until firstDayOfWeek - 1) {
        dayNames.add(weekdays[i])
    }
    CompositionLocalProvider(LocalContentColor provides colors.weekdayContentColor) {
        val textStyle =
            MaterialTheme.typography.bodyLarge
        ProvideTextStyle(value = textStyle) {
            Row(
                modifier = Modifier
                    .defaultMinSize(
                        minHeight = RecommendedSizeForAccessibility
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dayNames.forEach {
                    Box(
                        modifier = Modifier
                            .clearAndSetSemantics { contentDescription = it.first }
                            .size(
                                width = RecommendedSizeForAccessibility,
                                height = RecommendedSizeForAccessibility
                            ),
                        contentAlignment = Alignment.Center) {
                        Text(
                            text = it.second,
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Month(
    month: CustomCalendarMonth,
    onDateSelected: (dateInMillis: Long) -> Unit,
    today: CustomCalendarDate,
    stateData: CustomStateData,
    rangeSelectionEnabled: Boolean,
    dateValidator: (Long) -> Boolean,
    dateFormatter: CustomDatePickerFormatter,
    colors: CustomDatePickerColors
) {
    val rangeSelectionInfo: State<CustomSelectedRangeInfo?> = remember(rangeSelectionEnabled) {
        derivedStateOf {
            if (rangeSelectionEnabled) {
                CustomSelectedRangeInfo.calculateRangeInfo(
                    month,
                    stateData.selectedStartDate.value,
                    stateData.selectedEndDate.value
                )
            } else {
                null
            }
        }
    }

    val rangeSelectionDrawModifier = if (rangeSelectionEnabled) {
        Modifier.drawWithContent {
            rangeSelectionInfo.value?.let {
                customDrawRangeBackground(it, colors.dayInSelectionRangeContainerColor)
            }
            drawContent()
        }
    } else {
        Modifier
    }

    val defaultLocale = Locale.ENGLISH
    val startSelection = stateData.selectedStartDate
    val endSelection = stateData.selectedEndDate
    ProvideTextStyle(
        MaterialTheme.typography.bodyLarge
    ) {
        var cellIndex = 0
        Column(
            modifier = Modifier
                .requiredHeight(RecommendedSizeForAccessibility * MaxCalendarRows)
                .then(rangeSelectionDrawModifier),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(MaxCalendarRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(DaysInWeek) {
                        if (cellIndex < month.daysFromStartOfWeekToFirstOfMonth ||
                            cellIndex >=
                            (month.daysFromStartOfWeekToFirstOfMonth + month.numberOfDays)
                        ) {
                            // Empty cell
                            Spacer(
                                modifier = Modifier.requiredSize(
                                    width = RecommendedSizeForAccessibility,
                                    height = RecommendedSizeForAccessibility
                                )
                            )
                        } else {
                            val dayNumber = cellIndex - month.daysFromStartOfWeekToFirstOfMonth
                            val dateInMillis = month.startUtcTimeMillis +
                                    (dayNumber * MillisecondsIn24Hours)
                            val isToday = dateInMillis == today.utcTimeMillis
                            val startDateSelected =
                                dateInMillis == startSelection.value?.utcTimeMillis
                            val endDateSelected = dateInMillis == endSelection.value?.utcTimeMillis
                            val inRange = remember(rangeSelectionEnabled, dateInMillis) {
                                derivedStateOf {
                                    with(stateData) {
                                        rangeSelectionEnabled &&
                                                dateInMillis >= (selectedStartDate.value?.utcTimeMillis
                                            ?: Long.MAX_VALUE) &&
                                                dateInMillis <= (selectedEndDate.value?.utcTimeMillis
                                            ?: Long.MIN_VALUE)
                                    }
                                }
                            }
                            val dayContentDescription = dayContentDescription(
                                rangeSelectionEnabled = rangeSelectionEnabled,
                                isToday = isToday,
                                isStartDate = startDateSelected,
                                isEndDate = endDateSelected,
                                isInRange = inRange.value
                            )
                            val formattedDateDescription = formatWithSkeleton(
                                dateInMillis,
                                dateFormatter.selectedDateDescriptionSkeleton,
                                defaultLocale
                            )
                            Day(
                                modifier = Modifier,
                                selected = startDateSelected || endDateSelected,
                                onClick = { onDateSelected(dateInMillis) },
                                // Only animate on the first selected day. This is important to
                                // disable when drawing a range marker behind the days on an
                                // end-date selection.
                                animateChecked = startDateSelected,
                                enabled = remember(dateInMillis) {
                                    dateValidator.invoke(dateInMillis)
                                },
                                today = isToday,
                                inRange = inRange.value,
                                description = if (dayContentDescription != null) {
                                    "$dayContentDescription, $formattedDateDescription"
                                } else {
                                    formattedDateDescription
                                },
                                colors = colors
                            ) {
                                Text(
                                    text = (dayNumber + 1).toLocalString(),
                                    // The semantics are set at the Day level.
                                    modifier = Modifier.clearAndSetSemantics { },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        cellIndex++
                    }
                }
            }
        }
    }
}

@Composable
private fun dayContentDescription(
    rangeSelectionEnabled: Boolean,
    isToday: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    isInRange: Boolean
): String? {
    /*val descriptionBuilder = StringBuilder()
    if (rangeSelectionEnabled) {
        when {
            isStartDate -> descriptionBuilder.append(
                getString(string = Strings.DateRangePickerStartHeadline)
            )

            isEndDate -> descriptionBuilder.append(
                getString(string = Strings.DateRangePickerEndHeadline)
            )

            isInRange -> descriptionBuilder.append(
                getString(string = Strings.DateRangePickerDayInRange)
            )
        }
    }
    if (isToday) {
        if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append(", ")
        descriptionBuilder.append(getString(string = Strings.DatePickerTodayDescription))
    }*/
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Day(
    modifier: Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    animateChecked: Boolean,
    enabled: Boolean,
    today: Boolean,
    inRange: Boolean,
    description: String,
    colors: CustomDatePickerColors,
    content: @Composable () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSize(
                DateStateLayerWidth,
                DateStateLayerHeight
            )
            // Apply and merge semantics here. This will ensure that when scrolling the list the
            // entire Day surface is treated as one unit and holds the date semantics even when it's
            // not completely visible atm.
            .semantics(mergeDescendants = true) {
                text = AnnotatedString(description)
                role = Role.Button
            },
        enabled = enabled,
        shape = CircleShape,
        color = colors.dayContainerColor(
            selected = selected,
            enabled = enabled,
            animate = animateChecked
        ).value,
        contentColor = colors.dayContentColor(
            isToday = today,
            selected = selected,
            inRange = inRange,
            enabled = enabled,
        ).value,
        border = if (today && !selected) {
            BorderStroke(
                DateTodayContainerOutlineWidth,
                colors.todayDateBorderColor
            )
        } else {
            null
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearPicker(
    modifier: Modifier,
    onYearSelected: (year: Int) -> Unit,
    colors: CustomDatePickerColors,
    stateData: CustomStateData
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.bodyLarge
    ) {
        val currentYear = stateData.currentMonth.year
        val displayedYear = stateData.displayedMonth.year
        val lazyGridState =
            rememberLazyGridState(
                // Set the initial index to a few years before the current year to allow quicker
                // selection of previous years.
                initialFirstVisibleItemIndex = Integer.max(
                    0, displayedYear - stateData.yearRange.first - YearsInRow
                )
            )
        // Match the years container color to any elevated surface color that is composed under it.
        val containerColor = if (colors.containerColor == MaterialTheme.colorScheme.surface) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
        } else {
            colors.containerColor
        }
        val coroutineScope = rememberCoroutineScope()
        val scrollToEarlierYearsLabel = "DatePickerScrollToShowEarlierYears"
        val scrollToLaterYearsLabel = "DatePickerScrollToShowLaterYears"
        LazyVerticalGrid(
            columns = GridCells.Fixed(YearsInRow),
            modifier = modifier
                .background(containerColor)
                // Apply this to have the screen reader traverse outside the visible list of years
                // and not scroll them by default.
                .semantics {
                    verticalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
                },
            state = lazyGridState,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(YearsVerticalPadding)
        ) {
            items(stateData.yearRange.count()) {
                val selectedYear = it + stateData.yearRange.first
                val localizedYear = selectedYear.toLocalString()
                Year(
                    modifier = Modifier
                        .requiredSize(
                            width = SelectionYearContainerWidth,
                            height = SelectionYearContainerHeight
                        )
                        .semantics {
                            // Apply a11y custom actions to the first and last items in the years
                            // grid. The actions will suggest to scroll to earlier or later years in
                            // the grid.
                            customActions = if (lazyGridState.firstVisibleItemIndex == it ||
                                lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == it
                            ) {
                                customScrollActions(
                                    state = lazyGridState,
                                    coroutineScope = coroutineScope,
                                    scrollUpLabel = scrollToEarlierYearsLabel,
                                    scrollDownLabel = scrollToLaterYearsLabel
                                )
                            } else {
                                emptyList()
                            }
                        },
                    selected = selectedYear == displayedYear,
                    currentYear = selectedYear == currentYear,
                    onClick = { onYearSelected(selectedYear) },
                    description = "DatePickerNavigateToYearDescription"
                        .format(localizedYear),
                    colors = colors
                ) {
                    Text(
                        text = localizedYear,
                        // The semantics are set at the Year level.
                        modifier = Modifier.clearAndSetSemantics {},
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Year(
    modifier: Modifier,
    selected: Boolean,
    currentYear: Boolean,
    onClick: () -> Unit,
    description: String,
    colors: CustomDatePickerColors,
    content: @Composable () -> Unit
) {
    val border = remember(currentYear, selected) {
        if (currentYear && !selected) {
            // Use the day's spec to draw a border around the current year.
            BorderStroke(
                DateTodayContainerOutlineWidth,
                colors.todayDateBorderColor
            )
        } else {
            null
        }
    }
    Surface(
        selected = selected,
        onClick = onClick,
        // Apply and merge semantics here. This will ensure that when scrolling the list the entire
        // Year surface is treated as one unit and holds the date semantics even when it's not
        // completely visible atm.
        modifier = modifier.semantics(mergeDescendants = true) {
            text = AnnotatedString(description)
            role = Role.Button
        },
        shape = CircleShape,
        color = colors.yearContainerColor(selected = selected).value,
        contentColor = colors.yearContentColor(
            currentYear = currentYear,
            selected = selected
        ).value,
        border = border,
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

@Composable
private fun MonthsNavigation(
    modifier: Modifier,
    nextAvailable: Boolean,
    previousAvailable: Boolean,
    yearPickerVisible: Boolean,
    yearPickerText: String,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onYearPickerButtonClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(MonthYearHeight),
        horizontalArrangement = if (yearPickerVisible) {
            Arrangement.Start
        } else {
            Arrangement.SpaceBetween
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // A menu button for selecting a year.
        YearPickerMenuButton(
            onClick = onYearPickerButtonClicked,
            expanded = yearPickerVisible
        ) {
            Text(text = yearPickerText,
                modifier = Modifier.semantics {
                    // Make the screen reader read out updates to the menu button text as the user
                    // navigates the arrows or scrolls to change the displayed month.
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = yearPickerText
                })
        }
        // Show arrows for traversing months (only visible when the year selection is off)
        if (!yearPickerVisible) {
            Row {
                val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
                IconButton(onClick = onPreviousClicked, enabled = previousAvailable) {
                    Icon(
                        if (rtl) {
                            Icons.Filled.KeyboardArrowRight
                        } else {
                            Icons.Filled.KeyboardArrowLeft
                        },
                        contentDescription = "DatePickerSwitchToPreviousMonth"
                    )
                }
                IconButton(onClick = onNextClicked, enabled = nextAvailable) {
                    Icon(
                        if (rtl) {
                            Icons.Filled.KeyboardArrowLeft
                        } else {
                            Icons.Filled.KeyboardArrowRight
                        },
                        contentDescription = "DatePickerSwitchToNextMonth"
                    )
                }
            }
        }
    }
}

@Composable
private fun YearPickerMenuButton(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors =
        ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
        elevation = null,
        border = null,
    ) {
        content()
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription = if (expanded) {
                "DatePickerSwitchToDaySelection"
            } else {
                "DatePickerSwitchToYearSelection"
            },
            Modifier.rotate(if (expanded) 180f else 0f)
        )
    }
}

private fun customScrollActions(
    state: LazyGridState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) {
            false
        } else {
            coroutineScope.launch {
                state.scrollToItem(state.firstVisibleItemIndex - YearsInRow)
            }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) {
            false
        } else {
            coroutineScope.launch {
                state.scrollToItem(state.firstVisibleItemIndex + YearsInRow)
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

fun Int.toLocalString(): String {
    val formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH)
    // Eliminate any use of delimiters when formatting the integer.
    formatter.isGroupingUsed = false
    return formatter.format(this)
}

fun formatWithSkeleton(
    utcTimeMillis: Long,
    skeleton: String,
    locale: Locale
): String {
    val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CustomCalendarModelImpl.formatWithPattern(utcTimeMillis, pattern, locale)
    } else {
        CustomLegacyCalendarModelImpl.formatWithPattern(utcTimeMillis, pattern, locale)
    }
}


val RecommendedSizeForAccessibility = 48.dp
val MonthYearHeight = 56.dp
val DatePickerHorizontalPadding = 12.dp
val DatePickerModeTogglePadding = PaddingValues(end = 12.dp, bottom = 12.dp)

val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

val HeaderContainerHeight = 100.0.dp

val YearsVerticalPadding = 16.dp

const val MaxCalendarRows = 6
const val YearsInRow: Int = 3

val DateTodayContainerOutlineWidth = 1.0.dp

val SelectionYearContainerHeight = 36.0.dp
val SelectionYearContainerWidth = 72.0.dp

val DateStateLayerWidth = 40.0.dp
val DateStateLayerHeight = 40.0.dp

val ContainerWidth = 360.0.dp

val DurationShort2 = 100.0

val ContainerElevation = 6.0.dp
