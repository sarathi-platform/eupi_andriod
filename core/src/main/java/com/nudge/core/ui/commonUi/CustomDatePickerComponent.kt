@file:OptIn(ExperimentalMaterial3Api::class)

package com.nudge.core.ui.commonUi

//import com.nudge.core.ui.date_picker_component.datepicker.DatePicker
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.R
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.ui.date_picker_component.CustomDatePicker
import com.nudge.core.ui.date_picker_component.CustomDatePickerColors
import com.nudge.core.ui.date_picker_component.CustomDatePickerDefaults
import com.nudge.core.ui.date_picker_component.CustomDatePickerFormatter
import com.nudge.core.ui.date_picker_component.CustomDatePickerState
import com.nudge.core.ui.date_picker_component.DisplayMode
import com.nudge.core.ui.date_picker_component.rememberDatePickerState
import com.nudge.core.ui.theme.dimen_12_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_24_dp
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
            modifier = modifier.padding(all = 20.dp),
            shape = datePickerDialogProperties.shape,
            tonalElevation = datePickerDialogProperties.tonalElevation,
            properties = datePickerDialogProperties.properties,
            colors = DatePickerDefaults.colors(
                containerColor = searchFieldBg
            ),
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmButtonClicked()
                    },
                    content = { Text(stringResource(R.string.ok)) }
                )
            }
        ) {
            /*CustomVerticalSpacer()
            Text(
                text = datePickerProperties.state.selectedDateMillis.getDate(pattern = DD_MMM_YYYY_FORMAT),
                style = MaterialTheme.typography.headlineLarge,
                color = blueDark,
                modifier = Modifier.padding(horizontal = dimen_8_dp)
            )
            Divider(thickness = dimen_1_dp, color = Color.Black)*/
            /*DatePicker(onDateSelected = { yrs, month, day ->
                val calender = Calendar.getInstance()
                calender.set(yrs, month, day)
                val dateInLong = calender.timeInMillis
                datePickerProperties.state.setSelection(dateInLong)
                onConfirmButtonClicked()
            })*/

            CustomDatePicker(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberCustomDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = getCurrentTimeInMillis(),
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker
): CustomDatePickerState {
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
    colors: CustomDatePickerColors = CustomDatePickerDefaults.colors(
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
    val colors: CustomDatePickerColors,
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
    state: CustomDatePickerState,
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
    showModeToggle: Boolean = false,
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
    val state: CustomDatePickerState,
    val dateFormatter: CustomDatePickerFormatter,
    val dateValidator: (Long) -> Boolean,
    val title: (@Composable () -> Unit)?,
    val headline: (@Composable () -> Unit)?,
    val showModeToggle: Boolean,
    val colors: DatePickerColors
)


private val DatePickerTitlePadding =
    PaddingValues(start = dimen_24_dp, end = dimen_12_dp, top = dimen_16_dp)
private val DatePickerHeadlinePadding =
    PaddingValues(start = dimen_24_dp, end = dimen_12_dp, bottom = dimen_12_dp)

