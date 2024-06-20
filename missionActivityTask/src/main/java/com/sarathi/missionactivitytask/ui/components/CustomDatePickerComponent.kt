@file:OptIn(ExperimentalMaterial3Api::class)

package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.ui.theme.searchFieldBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerComponent(
    modifier: Modifier = Modifier,
    datePickerProperties: DatePickerProperties,
    datePickerDialogProperties: CustomDatePickerDialogProperties = rememberCustomDatePickerDialogProperties(),
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit
) {

    if (datePickerDialogProperties.getDatePickerDialogVisibilityState().value) {
        DatePickerDialog(
            modifier = modifier,
            shape = datePickerDialogProperties.shape,
            tonalElevation = datePickerDialogProperties.tonalElevation,
            properties = datePickerDialogProperties.properties,
            colors = datePickerDialogProperties.colors,
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmButtonClicked()
                    },
                    content = { Text("Ok") }
                )
            }
        ) {
            DatePicker(
                state = datePickerProperties.state,
                dateValidator = datePickerProperties.dateValidator,
                dateFormatter = datePickerProperties.dateFormatter,
                title = datePickerProperties.title,
                headline = datePickerProperties.headline,
                showModeToggle = datePickerProperties.showModeToggle,
                colors = datePickerDialogProperties.colors
            )
        }
    }


}

@Composable
fun rememberCustomDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = getCurrentTimeInMillis(),
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): DatePickerState {
    return rememberDatePickerState(
        initialSelectedDateMillis,
        initialDisplayedMonthMillis,
        yearRange,
        initialDisplayMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberCustomDatePickerDialogProperties(
    modifier: Modifier = Modifier,
    showDatePickerDialog: Boolean = false,
    shape: Shape = DatePickerDefaults.shape,
    tonalElevation: Dp = DatePickerDefaults.TonalElevation,
    colors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = searchFieldBg
    ),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false)
): CustomDatePickerDialogProperties {
    return CustomDatePickerDialogProperties(
        modifier = modifier,
        showDatePickerDialog = showDatePickerDialog,
        shape = shape,
        tonalElevation = tonalElevation,
        colors = colors,
        properties = properties
    )
}

@OptIn(ExperimentalMaterial3Api::class)
data class CustomDatePickerDialogProperties(
    val modifier: Modifier = Modifier,
    var showDatePickerDialog: Boolean = false,
    val shape: Shape,
    val tonalElevation: Dp,
    val colors: DatePickerColors,
    val properties: DialogProperties
) {

    private val showDatePickerDialogState = mutableStateOf(showDatePickerDialog)

    fun getDatePickerDialogVisibilityState() = showDatePickerDialogState


    fun show() {
        updateDatePickerDialog(true)
    }

    fun hide() {
        updateDatePickerDialog(false)
    }

    private fun updateDatePickerDialog(showDialog: Boolean) {
        this.showDatePickerDialogState.value = showDialog
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDatePickerProperties(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    dateFormatter: DatePickerFormatter = remember { DatePickerFormatter() },
    dateValidator: (Long) -> Boolean = { true },
    title: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerTitle(
            state,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerHeadline(
            state,
            dateFormatter,
            modifier = Modifier.padding(DatePickerHeadlinePadding)
        )
    },
    showModeToggle: Boolean = true,
    colors: DatePickerColors = DatePickerDefaults.colors()
): DatePickerProperties {

    return DatePickerProperties(
        modifier = modifier,
        state = state,
        dateFormatter = dateFormatter,
        dateValidator = dateValidator,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle,
        colors = colors
    )

}

@OptIn(ExperimentalMaterial3Api::class)
data class DatePickerProperties(
    val modifier: Modifier,
    val state: DatePickerState,
    val dateFormatter: DatePickerFormatter,
    val dateValidator: (Long) -> Boolean,
    val title: (@Composable () -> Unit)?,
    val headline: (@Composable () -> Unit)?,
    val showModeToggle: Boolean,
    val colors: DatePickerColors
)


private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

