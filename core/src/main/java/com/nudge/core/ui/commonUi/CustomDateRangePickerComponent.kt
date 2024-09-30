package com.nudge.core.ui.commonUi

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nudge.core.R
import com.nudge.core.showCustomToast
import com.nudge.core.ui.date_picker_component.CustomDatePickerColors
import com.nudge.core.ui.date_picker_component.CustomDatePickerDefaults
import com.nudge.core.ui.date_picker_component.CustomDatePickerFormatter
import com.nudge.core.ui.date_picker_component.CustomDateRangePicker
import com.nudge.core.ui.date_picker_component.CustomDateRangePickerDefaults
import com.nudge.core.ui.date_picker_component.CustomDateRangePickerState
import com.nudge.core.ui.date_picker_component.rememberDateRangePickerState
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.searchFieldBg
import com.nudge.core.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.ui.theme.white

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePickerBottomSheetComponent(
    customDateRangePickerBottomSheetProperties: CustomDateRangePickerBottomSheetProperties = rememberDateRangePickerBottomSheetProperties(),
    dateRangePickerProperties: DateRangePickerProperties = rememberDateRangePickerProperties(),
    sheetHeight: SheetHeight = SheetHeight.Default,
    onSheetConfirmButtonClicked: () -> Unit,
    content: @Composable () -> Unit
) {

    val localConfiguration = LocalConfiguration.current
    val context = LocalContext.current

    val screenHeightDp = localConfiguration.screenHeightDp.dp

    val height = remember {
        when (sheetHeight) {
            is SheetHeight.Default -> {
                screenHeightDp
            }

            is SheetHeight.CustomSheetHeight -> {
                screenHeightDp - sheetHeight.height
            }
        }
    }
    var isVisibleDateRangePickerDialog by remember {
        mutableStateOf(true)
    }
    /*Scaffold {
        val a = it.calculateBottomPadding()
        content()
    }*/

    //TDOO Fix the crash for bottom sheet.
    ModalBottomSheetLayout(
        modifier = customDateRangePickerBottomSheetProperties.modifier,
        sheetShape = customDateRangePickerBottomSheetProperties.sheetShape,
        sheetState = customDateRangePickerBottomSheetProperties.sheetState,
        sheetGesturesEnabled = true,
        sheetElevation = customDateRangePickerBottomSheetProperties.sheetElevation,
        sheetBackgroundColor = customDateRangePickerBottomSheetProperties.sheetBackgroundColor,
        sheetContentColor = customDateRangePickerBottomSheetProperties.sheetContentColor,
        scrimColor = customDateRangePickerBottomSheetProperties.scrimColor,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .padding(top = dimen_14_dp)
                    .background(searchFieldBg)
            ) {
                CustomDateRangePicker(
                    state = dateRangePickerProperties.state,
                    modifier = dateRangePickerProperties.modifier,
                    dateFormatter = dateRangePickerProperties.dateFormatter,
                    dateValidator = dateRangePickerProperties.dateValidator,
                    title = dateRangePickerProperties.title,
                    headline = dateRangePickerProperties.headline,
                    showModeToggle = dateRangePickerProperties.showModeToggle,
                    colors = dateRangePickerProperties.colors,
                )


                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentWidth()
                        .padding(end = dimen_10_dp, bottom = dimen_10_dp)
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            if (dateRangePickerProperties.state.selectedStartDateMillis != null) {
                                onSheetConfirmButtonClicked()
                            } else {
                                showCustomToast(
                                    context = context,
                                    msg = "Please select the start date"
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueDark
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.ok),
                            color = white,
                            style = smallTextStyleWithNormalWeight
                        )
                    }
                }

            }
        }
    ) {
        content()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberCustomDateRangePickerSheetState(
    initialValue: ModalBottomSheetValue = ModalBottomSheetValue.Hidden,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmValueChange: (ModalBottomSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = true,
): ModalBottomSheetState {

    return rememberModalBottomSheetState(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        skipHalfExpanded = skipHalfExpanded
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDateRangePickerProperties(
    state: CustomDateRangePickerState = rememberDateRangePickerState(),
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
        CustomDateRangePickerDefaults.DateRangePickerHeadline(
            state,
            dateFormatter,
            modifier = Modifier.padding(DateRangePickerHeadlinePadding)
        )
    },
    showModeToggle: Boolean = true,
    colors: CustomDatePickerColors = CustomDatePickerDefaults.colors(
        containerColor = searchFieldBg,
        todayDateBorderColor = blueDark,
        dayInSelectionRangeContainerColor = blueDark.copy(0.5f),
        selectedDayContainerColor = blueDark,
        selectedDayContentColor = white
    )
): DateRangePickerProperties {
    return DateRangePickerProperties(
        state = state,
        modifier = modifier,
        dateFormatter = dateFormatter,
        dateValidator = dateValidator,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
data class DateRangePickerProperties(
    val modifier: Modifier,
    val state: CustomDateRangePickerState,
    val dateFormatter: CustomDatePickerFormatter,
    val dateValidator: (Long) -> Boolean,
    val title: (@Composable () -> Unit)?,
    val headline: (@Composable () -> Unit)?,
    val showModeToggle: Boolean,
    val colors: CustomDatePickerColors
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberDateRangePickerBottomSheetProperties(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = rememberCustomDateRangePickerSheetState(),
    sheetShape: Shape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = searchFieldBg,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
): CustomDateRangePickerBottomSheetProperties {
    return CustomDateRangePickerBottomSheetProperties(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor
    )
}

@OptIn(ExperimentalMaterialApi::class)
data class CustomDateRangePickerBottomSheetProperties(
    val modifier: Modifier = Modifier,
    val sheetState: ModalBottomSheetState,
    val sheetShape: Shape,
    val sheetElevation: Dp,
    val sheetBackgroundColor: Color,
    val sheetContentColor: Color,
    val scrimColor: Color,
)

private val DateRangePickerTitlePadding = PaddingValues(start = 12.dp, end = 12.dp)
private val DateRangePickerHeadlinePadding =
    PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp)

sealed class SheetHeight() {

    object Default : SheetHeight()

    /**
     *  @param height The value which should be subtracted from the screen height.
     **/
    data class CustomSheetHeight(val height: Dp) : SheetHeight()

}